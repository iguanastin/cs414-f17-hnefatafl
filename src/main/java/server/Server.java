package server;

import common.event.connection.ClientDisconnectEvent;
import common.event.connection.ConnectAcceptedEvent;
import common.event.connection.HeartbeatEvent;
import common.event.invite.*;
import common.event.login.*;
import common.game.FinishedMatch;
import common.game.Match;
import common.game.MatchStatus;
import common.*;
import common.event.match.*;
import common.event.profile.*;
import org.mindrot.jbcrypt.BCrypt;
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
     * SQL Statement used to create the invitations table
     */
    private static final String CREATE_INVITES_TABLE = "CREATE TABLE invites(p1_id INT, p2_id INT, CONSTRAINT fk_p1i FOREIGN KEY (p1_id) REFERENCES users(id), CONSTRAINT fk_p2i FOREIGN KEY (p2_id) REFERENCES users(id));";

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
     * SQL Statement to drop the invitations table
     */
    private static final String DROP_INVITES_TABLE = "DROP TABLE IF EXISTS invites;";

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
            logger.error("Error initializing data from db", e);
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
            password = BCrypt.hashpw(password, BCrypt.gensalt());
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
            s.executeQuery("SELECT * FROM invites;");
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

        s.executeUpdate(DROP_INVITES_TABLE);
        s.executeUpdate(CREATE_INVITES_TABLE);

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
            handleLoginRequestEvent((LoginRequestEvent) event, client);
        } else if (event instanceof RegisterRequestEvent) {
            handleRegisterRequestEvent((RegisterRequestEvent) event, client);
        }

        //--------------------------------------------------------------------------------------------------------------
        if (user != null) {
            //This client is logged in as user

            if (event instanceof PlayerMoveEvent) {
                handlePlayerMoveEvent((PlayerMoveEvent) event, user);
            } else if (event instanceof InviteUserEvent) {
                handleInviteUserEvent((InviteUserEvent) event, user);
            } else if (event instanceof RequestActiveInfoEvent) {
                handleRequestActiveInfoEvent(user);
            } else if (event instanceof RequestProfileEvent) {
                handleRequestProfileEvent((RequestProfileEvent) event, user);
            } else if (event instanceof AcceptInviteEvent) {
                handleAcceptInviteEvent((AcceptInviteEvent) event, user);
            } else if (event instanceof DeclineInviteEvent) {
                handleDeclineInviteEvent((DeclineInviteEvent) event, user);
            } else if (event instanceof UnregisterRequestEvent) {
                try {
                    unregister(user);
                } catch (IOException e) {
                    logger.error("Error unregistering user", e);
                }
            }
        }
    }

    private void handleDeclineInviteEvent(DeclineInviteEvent event, User user) {
        try {
            declineInvitation(getUser(event.getSenderID()), user);
        } catch (SQLException e) {
            logger.error("Error declining invitation from user: " + event.getSenderID(), e);
        }
    }

    private void handleAcceptInviteEvent(AcceptInviteEvent event, User user) {
        try {
            acceptInvitation(getUser(event.getSenderID()), user);
        } catch (SQLException e) {
            logger.error("Error accepting invitation from user: " + event.getSenderID(), e);
        }
    }

    private void handleRegisterRequestEvent(RegisterRequestEvent event, ConnectionToClient client) {
        String registerEmail = event.getEmail();
        String registerUserName = event.getUsername();
        String registerPassword = event.getPassword();
        User user = null;

        if (registerUserName.equals("") || registerPassword.equals("") || registerEmail.equals("")) {
            try {
                client.sendToClient(new RegisterFailedEvent(registerEmail, registerUserName, "Username, Password, and email are required fields."));
            } catch (IOException e) {
                logger.error("Error sending register failed event", e);
            }
        } else {
            boolean loginFalied = false;

            for (User u : users) {
                if (u.getName().equalsIgnoreCase(registerUserName) || u.getEmail().equalsIgnoreCase(registerEmail)) {
                    //Username or email is taken... Try again
                    //TODO: break this out into 2 errors
                    loginFalied = true;
                    try {
                        client.sendToClient(new RegisterFailedEvent(registerEmail, registerUserName, "Username or email is already taken."));
                    } catch (IOException e) {
                        logger.error("Error sending register failed event", e);
                    }
                }
            }
            if (!loginFalied) {
                //Compare to standard email regex RFC 5322
                if (!registerEmail.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
                    try {
                        client.sendToClient(new RegisterFailedEvent(registerEmail, registerUserName, "Please enter a valid email."));
                    } catch (IOException e) {
                        logger.error("Error sending register failed event", e);
                    }
                } else {
                    user = createUser(registerEmail, registerUserName, registerPassword);
                    if (user != null) {
                        user.setClient(client);
                        try {
                            client.sendToClient(new RegisterSuccessEvent(user.getEmail(), user.getName(), user.getPassword(), user.getId()));
                        } catch (IOException e) {
                            logger.error("Error sending register success event", e);
                        }
                    } else {
                        //TODO: Send error creating user
                    }
                }
            }
        }
    }

    private void handleLoginRequestEvent(LoginRequestEvent event, ConnectionToClient client) {
        ///authenticate((LoginRequestEvent) event, client);
        //Auth not working yet, just let them in.
        String loginUserName = event.getUsername();
        String loginPassword = event.getPassword();
        User user = null;

        for (User u : users) {
            if (u.getName().equals(loginUserName)) {
                if (u.isUnregistered()) {
                    try {
                        client.sendToClient(new LoginFailedEvent("This user is unregistered and cannot be used"));
                    } catch (IOException e) {
                        logger.error("Error sending login failed even", e);
                    }
                } else if (BCrypt.checkpw(loginPassword, u.getPassword())) {
                    //Password provided at login matches hashed password of user with the same name
                    user = u;
                } else {
                    //Password does not match. Tell the user
                    try {
                        client.sendToClient(new LoginFailedEvent("Password Provided for this Username was incorrect."));
                    } catch (IOException e) {
                        logger.error("Error sending login failed event", e);
                    }
                }

                break;
            }
        }

        if (user == null) {
            try {
                client.sendToClient(new LoginFailedEvent(loginUserName));
            } catch (IOException e) {
                logger.error("Error sending login failed event", e);
            }
        } else {
            user.setClient(client);
            user.send(new LoginSuccessEvent(user.getName(), user.getId()));
        }
    }

    private void handleRequestProfileEvent(RequestProfileEvent event, User user) {
        try {
            User target = getUser(event.getUsername());
            if (target != null) {
                user.send(new SendProfileEvent(new Profile(getMatchHistory(target.getId()), target.getId(), target.getName())));
            } else {
                user.send(new NoSuchUserEvent(event.getUsername()));
            }
        } catch (SQLException e) {
            logger.error("Error getting match history for user: " + event.getUsername(), e);
        }
    }

    private void handleRequestActiveInfoEvent(User user) {
        for (Match match : matches) {
            if (match.getDefender() == user.getId() || match.getAttacker() == user.getId()) {
                user.send(new MatchStartEvent(match));
            }
        }
        for (Invitation invite : getInvitesFor(user)) {
            user.send(new InviteReceivedEvent(invite));
        }
    }

    private void handleInviteUserEvent(InviteUserEvent event, User user) {
        User enemy = getUser(event.getUsername());
        if (enemy != null && enemy != user) {
            if (getMatch(user.getId(), enemy.getId()) == null) {
                try {
                    inviteUser(user, enemy);
                } catch (SQLException e) {
                    //TODO: Send error stating that the user has already been invited or has a pending invite for you
                }
            } else {
                //TODO Send error stating that a match already exists between these two users
            }
        } else {
            user.send(new NoSuchUserEvent(event.getUsername()));
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

    private FinishedMatch endMatch(Match match) {
        int reason;
        if (match.getStatus() == MatchStatus.DEFENDER_WIN) {
            reason = FinishedMatch.DEFENDER_WIN;
        } else if (match.getStatus() == MatchStatus.ATTACKER_WIN) {
            reason = FinishedMatch.ATTACKER_WIN;
        } else {
            logger.error("Match is not in an end-state, cannot be ended");
            return null;
        }

        return endMatch(match, reason);
    }

    /**
     * Ends a match and notifies users
     *
     * @param match Match to end
     */
    private FinishedMatch endMatch(Match match, int reason) {
        logger.info("Match finished " + match.getAttacker() + "v" + match.getDefender());

        synchronized (matches) {
            matches.remove(match);
        }

        int winner;
        if (reason == FinishedMatch.ATTACKER_WIN || reason == FinishedMatch.DEFENDER_QUIT) {
            winner = match.getAttacker();
        } else {
            winner = match.getDefender();
        }

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

        return createMatchHistory(match, reason, winner);
    }

    private FinishedMatch createMatchHistory(Match match, int matchResult, int matchWinner) {
        try {
            PreparedStatement s = dbConnection.prepareStatement("INSERT INTO played(id, p1_id, p2_id, end_result, winner_id) VALUES (?,?,?,?,?);");

            int id = -1;

            //Get last used ID from db
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT TOP 1 id FROM played ORDER BY id DESC;");
            if (rs.next()) id = rs.getInt("id");

            id++;

            s.setInt(1, id);
            s.setInt(2, match.getAttacker());
            s.setInt(3, match.getDefender());
            s.setInt(4, matchResult);
            s.setInt(5, matchWinner);

            s.executeUpdate();
            s.close();

            return new FinishedMatch(id, match.getAttacker(), match.getDefender(), matchWinner, matchResult);
        } catch (SQLException e) {
            logger.error("Error committing game history to db", e);
            return null;
        }
    }

    private ArrayList<FinishedMatch> getMatchHistory(int userID) throws SQLException {
        ArrayList<FinishedMatch> matches = new ArrayList<>();

        PreparedStatement s = dbConnection.prepareStatement("SELECT * FROM played WHERE p1_id=? OR p2_id=?");
        s.setInt(1, userID);
        s.setInt(2, userID);

        ResultSet rs = s.executeQuery();
        while (rs.next()) {
            matches.add(new FinishedMatch(rs.getInt("id"), rs.getInt("p1_id"), rs.getInt("p2_id"), rs.getInt("winner_id"), rs.getInt("end_result")));
        }

        return matches;
    }

    private Invitation inviteUser(User sender, User target) throws SQLException {
        PreparedStatement s = dbConnection.prepareStatement("SELECT * FROM invites WHERE (p1_id=? AND p2_id=?) OR (p2_id=? AND p1_id=?)");
        s.setInt(1, sender.getId());
        s.setInt(2, target.getId());
        s.setInt(3, sender.getId());
        s.setInt(4, target.getId());
        ResultSet rs = s.executeQuery();
        if (rs.next()) return null;
        rs.close();
        s.close();

        s = dbConnection.prepareStatement("INSERT INTO invites(p1_id, p2_id) VALUES (?,?);");
        s.setInt(1, sender.getId());
        s.setInt(2, target.getId());
        s.executeUpdate();
        s.close();

        Invitation invite = new Invitation(sender.getId(), target.getId());
        if (target.isLoggedIn()) target.send(new InviteReceivedEvent(invite));

        return invite;
    }

    private void declineInvitation(Invitation invite) throws SQLException {
        declineInvitation(getUser(invite.getSenderID()), getUser(invite.getTargetID()));
    }

    private void declineInvitation(User sender, User target) throws SQLException {
        deleteInvitation(sender, target);

        if (sender.isLoggedIn()) sender.send(new InviteDeclinedEvent(new Invitation(sender.getId(), target.getId())));
    }

    private void deleteInvitation(User sender, User target) throws SQLException {
        PreparedStatement s = dbConnection.prepareStatement("DELETE FROM invites WHERE p1_id=? AND p2_id=?;");
        s.setInt(1, sender.getId());
        s.setInt(2, target.getId());
        s.executeUpdate();
    }

    private void acceptInvitation(User sender, User target) throws SQLException {
        deleteInvitation(sender, target);

        if (sender.isLoggedIn()) sender.send(new InviteAcceptedEvent(new Invitation(sender.getId(), target.getId())));

        startMatch(sender, target);
    }

    private ArrayList<Invitation> getInvitesFor(User target) {
        ArrayList<Invitation> invites = new ArrayList<>();

        try {
            PreparedStatement s = dbConnection.prepareStatement("SELECT * FROM invites WHERE p2_id=?;");
            s.setInt(1, target.getId());
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                invites.add(new Invitation(rs.getInt("p1_id"), rs.getInt("p2_id")));
            }

            return invites;
        } catch (SQLException e) {
            logger.error("Error getting invitations from db", e);
            return invites;
        }
    }

    private void unregister(User user) throws IOException {
        user.unregister();
        commitChangedUser(user);
        user.getClient().close();
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
    public static void main(String[] args) {
        int port = 54321;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        try {
            new Server(port);
        } catch (SQLException e) {
            System.exit(1);
        }
    }

}
