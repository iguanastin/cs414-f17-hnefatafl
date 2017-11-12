package server;

import common.game.Match;
import common.game.MatchStatus;
import common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Server extends AbstractServer {

    /**
     * SQL Statement to create the users table
     */
    private static final String CREATE_USERS_TABLE = "CREATE TABLE users(id INT PRIMARY KEY AUTO_INCREMENT, name NVARCHAR(20) NOT NULL UNIQUE, email NVARCHAR(64) NOT NULL, pass NVARCHAR(64) NOT NULL, deleted BOOL DEFAULT FALSE);";
    /**
     * SQL Statement to create the games table
     */
    private static final String CREATE_GAMES_TABLE = "CREATE TABLE games(p1_id INT, p2_id INT, end_result INT DEFAULT 0, winner_id INT DEFAULT 0, p1_turn BOOL DEFAULT TRUE, board_id INT, CONSTRAINT fk_p1g FOREIGN KEY (p1_id) REFERENCES users(id), CONSTRAINT fk_p2g FOREIGN KEY (p2_id) REFERENCES users(id));";
    /**
     * SQL Statement to create game history table
     */
    private static final String CREATE_HISTORY_TABLE = "CREATE TABLE played(id INT AUTO_INCREMENT PRIMARY KEY, p1_id INT, p2_id INT, end_result INT, winner_id INT, CONSTRAINT fk_p1p FOREIGN KEY (p1_id) REFERENCES users(id), CONSTRAINT fk_p2p FOREIGN KEY (p2_id) REFERENCES users(id));";

    /**
     * SQL Statement to drop the users table
     */
    private static final String DROP_USERS_TABLE = "DROP TABLE IF EXISTS users;";
    /**
     * SQL Statement to drop the games table
     */
    private static final String DROP_GAMES_TABLE = "DROP TABLE IF EXISTS games;";
    /**
     * SQL Statement to drop the game history table
     */
    private static final String DROP_HISTORY_TABLE = "DROP TABLE IF EXISTS played;";

    /**
     * SQL Database connection to the database
     */
    private Connection dbConnection;

    /**
     * Path to the database dump
     */
    private static final String DB_PATH = "./hnefatafl";

    /**
     * List of all active client connections
     */
    private final ArrayList<ConnectionToClient> connections = new ArrayList<>();

    /**
     * List of all users present in the system. Users are not guaranteed to be currently connected.
     */
    private final ArrayList<User> users = new ArrayList<>();

    /**
     * List of all matches currently in progress
     */
    private final ArrayList<Match> matches = new ArrayList<>();

    /**
     * The SLF4J logger used to log debug/info/errors for this server.
     */
    private final Logger logger = LoggerFactory.getLogger(Server.class);


    /**
     * Server constructor. Creates a server and starts it listening on the specified port.
     *
     * @param port Port to start listening on
     * @throws SQLException If database connection fails. (Usually caused by concurrent access)
     */
    public Server(int port) throws SQLException {
        super(port);
        logger.info("Creating server on port: " + port + "...");

        try {
            listen();
            logger.info("Listening for connections...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        connectToDB();

        initHeartbeatThread();

        //TODO: Remove this, testing purposes
        createUser("test@test.test", "user1", "1234");
        createUser("test2@test.test", "user2", "1234");
        createUser("test3@test.test", "user3", "1234");
    }

    /**
     * Attempts to start a match between two given users
     *
     * @param player1 The user who initiates the match
     * @param player2 The user who accepts the match
     * @return True if the match was initialized, false if there is already a match in progress between these two users
     */
    private boolean startMatch(User player1, User player2) {
        synchronized (matches) {
            if (getMatch(player1.getId(), player2.getId()) == null) {
                Match match = new Match(player1.getId(), player2.getId());
                matches.add(match);

                //Notify users
                if (player1.isLoggedIn()) {
                    try {
                        player1.getClient().sendToClient(new MatchStartEvent(match));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (player2.isLoggedIn()) {
                    try {
                        player2.getClient().sendToClient(new MatchStartEvent(match));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Finds an active match between two given users
     *
     * @param p1id ID of a user
     * @param p2id ID of a different user
     * @return Null if p1id==p2id or no match currently exists between the two users
     */
    private Match getMatch(final int p1id, final int p2id) {
        if (p1id == p2id) return null;

        synchronized (matches) {
            for (Match match : matches) {
                final int a = match.getAttacker();
                final int d = match.getDefender();
                if ((a == p1id && d == p2id) || (a == p2id && d == p1id)) return match;
            }
        }

        return null;
    }

    /**
     * Connects to the database and initializes it if necessary. Loads users, boards, games, etc. from db.
     *
     * @throws SQLException If the database is already in use or encounters an error otherwise.
     */
    private void connectToDB() throws SQLException {
        logger.info("Initializing database ocnnection...");
        dbConnection = DriverManager.getConnection("jdbc:h2:" + DB_PATH);
        if (!isDatabaseInitialized()) initDatabaseTables();

        try (Statement s = dbConnection.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT * FROM users;");
            while (rs.next()) {
                users.add(new User(rs.getInt("id"), rs.getNString("email"), rs.getNString("name"), rs.getNString("pass"), rs.getBoolean("deleted")));
            }
            s.close();
        } catch (SQLException e) {
            logger.error("Error initial data from db", e);
        }
    }

    /**
     * Commits any changes made to a User object to the database
     *
     * @param user User to be committed to the database
     */
    private void commitChangedUser(User user) {
        logger.info("Committing changed user: " + user);
        try (PreparedStatement s = dbConnection.prepareStatement("UPDATE users SET name=?, email=?, pass=?, deleted=? WHERE id=?;")) {
            s.setNString(1, user.getName());
            s.setNString(2, user.getEmail());
            s.setNString(3, user.getPassword());
            s.setBoolean(4, user.isUnregistered());
            s.setInt(5, user.getId());
            s.executeUpdate();
            s.close();
        } catch (SQLException e) {
            logger.error("Error committing user changes to db", e);
        }
    }

    /**
     * Creates a User with the given info and commits them to the database.
     *
     * @param email    User's email
     * @param name     Unique username
     * @param password Salted+Hashed password ready to be stored in the db
     * @return A user created with the information provided. Null if there was an error creating the user in the database
     */
    private User createUser(String email, String name, String password) {
        if (getUser(name) != null) return null;
        logger.info("Creating new User: " + name);
        try (PreparedStatement s = dbConnection.prepareStatement("INSERT INTO users(name, email, pass) VALUES (?, ?, ?);")) {
            s.setNString(1, name);
            s.setNString(2, email);
            s.setNString(3, password);
            s.executeUpdate();
            s.close();

            PreparedStatement ps = dbConnection.prepareStatement("SELECT * FROM users WHERE name=?");
            ps.setNString(1, name);
            ResultSet rs = ps.executeQuery();
            rs.next();
            User user = new User(rs.getInt("id"), rs.getNString("email"), rs.getNString("name"), rs.getNString("pass"), rs.getBoolean("deleted"));
            synchronized (users) {
                users.add(user);
            }
            ps.close();
            return user;
        } catch (SQLException e) {
            logger.error("Error creating new user", e);
            return null;
        }
    }

    /**
     * Initializes the heartbeat thread that sends a heartbeat message to all clients every 60 seconds.
     */
    private void initHeartbeatThread() {
        logger.info("Initializing heartbeat thread...");
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!connections.isEmpty()) {
                    logger.info("Sending hearbeat to " + connections.size() + " connections...");

                    synchronized (connections) {
                        for (ConnectionToClient client : connections) {
                            try {
                                client.sendToClient(new HeartbeatEvent());
                            } catch (IOException e) {
                                logClient(client, "Failed heartbeat. Disconnecting");
                                try {
                                    client.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }, 60000, 60000);
    }

    /**
     * Checks to see if the database has been initialized yet.
     *
     * @return True if the database is ready to use. False if the database is invalid or tables haven't been initialized.
     */
    private boolean isDatabaseInitialized() {
        try (Statement s = dbConnection.createStatement()) {
            s.executeQuery("SELECT * FROM users;");
            s.executeQuery("SELECT * FROM games;");
            s.executeQuery("SELECT * FROM played;");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Initializes the database tables.
     *
     * @throws SQLException When any of the database calls fail.
     */
    private void initDatabaseTables() throws SQLException {
        Statement s = dbConnection.createStatement();

        s.executeUpdate(DROP_USERS_TABLE);
        s.executeUpdate(CREATE_USERS_TABLE);

        s.executeUpdate(DROP_GAMES_TABLE);
        s.executeUpdate(CREATE_GAMES_TABLE);

        s.executeUpdate(DROP_HISTORY_TABLE);
        s.executeUpdate(CREATE_HISTORY_TABLE);

        s.close();
    }

    /**
     * Callback method called each time a client connection sends a "message". Message object type is not guaranteed.
     *
     * @param msg    Message object that the client sent
     * @param client The client from which the message object originated
     */
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        logClient(client, msg);

        if (msg instanceof Event) handleEventFromClient((Event) msg, client);
    }

    /**
     * Central method for flow of client input in the form of Events.
     * Called each time an Event message is received from a client.
     *
     * @param event  Event that was received from the client.
     * @param client Client from which the event originated.
     */
    private void handleEventFromClient(Event event, ConnectionToClient client) {
        User user = getUser(client);

        if (event instanceof ClientDisconnectEvent) {
            clientDisconnected(client);
        } else if (event instanceof LoginRequestEvent) {
            for (User u : users) {
                //TODO: Compare password as well
                if (u.getName().equals(((LoginRequestEvent) event).getUsername())) {
                    user = u;
                    break;
                }
            }

            if (user == null) {
                try {
                    client.sendToClient(new LoginFailedEvent(((LoginRequestEvent) event).getUsername()));
                } catch (IOException e) {
                    logger.error("Error sending login failed event", e);
                }
            } else {
                user.setClient(client);
                user.send(new LoginSuccessEvent(user.getName(), user.getId()));
            }
        }

        //Event handling for logged in clients
        //--------------------------------------------------------------------------------------------------------------
        if (user != null) {
            if (event instanceof PlayerMoveEvent) {
                handlePlayerMoveEvent((PlayerMoveEvent) event, user);
            } else if (event instanceof InviteToMatchEvent) {
                User enemy = getUser(((InviteToMatchEvent) event).getName());
                if (enemy != null && getMatch(user.getId(), enemy.getId()) == null) {
                    startMatch(user, enemy);
                }

                //TODO Make this actually invite and check for bad cases
            } else if (event instanceof RequestCurrentGamesEvent) {
                for (Match match : matches) {
                    if (match.getDefender() == user.getId() || match.getAttacker() == user.getId()) {
                        user.send(new MatchStartEvent(match));
                    }
                }
            }
        }
    }

    /**
     * Handles a player move attempt from a given User. Modifies the match in question and notifies both players of changes.
     *
     * @param event Event received from the player
     * @param user  The player that requested this move
     */
    private void handlePlayerMoveEvent(PlayerMoveEvent event, User user) {
        Match match = getMatch(user.getId(), event.getEnemyId());

        if (match != null) {
            if (match.getCurrentPlayer() == user.getId()) {
                if (!match.isValidMove(event.getFromRow(), event.getFromCol(), event.getToRow(), event.getToCol())) {
                    user.send(new PlayerMoveFailedEvent(PlayerMoveFailedReason.INVALID_MOVE));
                    return;
                }

                match.makeMove(event.getFromRow(), event.getFromCol(), event.getToRow(), event.getToCol());
                final boolean end = match.isOver();
                if (!end) match.swapTurn();

                notifyMatchUpdate(match);
                if (end) {
                    endMatch(match);
                }
            } else {
                user.send(new PlayerMoveFailedEvent(PlayerMoveFailedReason.NOT_YOUR_TURN));
            }
        } else {
            user.send(new PlayerMoveFailedEvent(PlayerMoveFailedReason.NO_MATCH));
        }
    }

    /**
     * Notifies users in the match of an update to the match state
     *
     * @param match Match to notify users in
     */
    private void notifyMatchUpdate(Match match) {
        User user = getUser(match.getAttacker());
        if (user != null && user.isLoggedIn()) {
            user.resetOutputStream();
            user.send(new MatchUpdateEvent(match));
        }
        user = getUser(match.getDefender());
        if (user != null && user.isLoggedIn()) {
            user.resetOutputStream();
            user.send(new MatchUpdateEvent(match));
        }
    }

    /**
     * Ends a match and notifies users
     *
     * @param match Match to end
     */
    private void endMatch(Match match) {
        logger.info("Match finished " + match.getAttacker() + "v" + match.getDefender());

        synchronized (matches) {
            matches.remove(match);
        }

        int matchResult = 0; //TODO Set up actual match end enums
        int matchWinner = match.getAttacker();
        if (match.getStatus().equals(MatchStatus.DEFENDER_WIN)) matchWinner = match.getDefender();

        createMatchHistory(match, matchResult, matchWinner);

        User user = getUser(match.getAttacker());
        if (user != null && user.isLoggedIn()) {
            user.resetOutputStream();
            user.send(new MatchFinishEvent(match));
        }
        user = getUser(match.getDefender());
        if (user != null && user.isLoggedIn()) {
            user.resetOutputStream();
            user.send(new MatchFinishEvent(match));
        }
    }

    private void createMatchHistory(Match match, int matchResult, int matchWinner) {
        try {
            PreparedStatement s = dbConnection.prepareStatement("INSERT INTO played(p1_id, p2_id, end_result, winner_id) VALUES (?,?,?,?);");

            s.setInt(1, match.getAttacker());
            s.setInt(2, match.getDefender());
            s.setInt(3, matchResult);
            s.setInt(4, matchWinner);

            s.executeUpdate();

            s.close();
        } catch (SQLException e) {
            logger.error("Error committing game history to db", e);
        }
    }

    /**
     * Callback method called each time a new client connects.
     * <p>
     * Registers the client with the server.
     *
     * @param client The client that connected
     */
    @Override
    protected synchronized void clientConnected(ConnectionToClient client) {
        logClient(client, "Connected");
        synchronized (connections) {
            connections.add(client);
        }

        try {
            client.sendToClient(new ConnectAcceptedEvent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback method called each time a client disconnects.
     *
     * @param client The client that disconnected
     */
    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        User user = getUser(client);
        if (user != null) user.setClient(null);

        logClient(client, "Disconnected");
        synchronized (connections) {
            connections.remove(client);
        }
    }

    /**
     * Callback method called when an exception is thrown for a client connection.
     * <p>
     * Note: A client that disconnects erroneously without a disconnect event will throw an exception.
     *
     * @param client    The client that raised the exception.
     * @param exception The exception thrown.
     */
    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        logClient(client, "Threw exception: " + exception.getLocalizedMessage());
        clientDisconnected(client);
    }

    /**
     * Logs a message in the context of a client connection.
     *
     * @param client Client which this message is being logged with
     * @param msg    Message being logged
     */
    private void logClient(ConnectionToClient client, Object msg) {
        logger.info("[" + client + "]: " + msg);
    }

    /**
     * Retrieves a user object for a given client connection if the client connection is currently logged in to the system.
     *
     * @param client Client to match user with.
     * @return The user object that represents the client. Null if the client has not logged in yet.
     */
    private User getUser(ConnectionToClient client) {
        synchronized (users) {
            for (User user : users) {
                if (user.getClient() == client) return user;
            }
        }

        return null;
    }

    /**
     * Attempts to find a known user from a given user ID
     *
     * @param id ID of user to find
     * @return User with the given ID, null if no such user exists
     */
    private User getUser(int id) {
        synchronized (users) {
            for (User user : users) {
                if (user.getId() == id) return user;
            }
        }

        return null;
    }

    /**
     * Attempts to find a known user with a given name
     *
     * @param name Name to search for
     * @return User with given name, null if no such user exists
     */
    private User getUser(String name) {
        synchronized (users) {
            for (User user : users) {
                if (user.getName().equalsIgnoreCase(name)) return user;
            }
        }

        return null;
    }

    /**
     * Constructs and starts a server on port 54321
     *
     * @param args Command line arguments
     * @throws SQLException When server is unable to connect to the database correctly, or the database throws an exception on initialization.
     */
    public static void main(String[] args) throws SQLException {
        int port = 54321;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new Server(port);
    }

}
