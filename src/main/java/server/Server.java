package server;

import common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Server extends AbstractServer {

    /**
     * SQL Statement to create the users table
     */
    private static final String CREATE_USERS_TABLE = "CREATE TABLE users(id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, name NVARCHAR(20) NOT NULL UNIQUE, pass NVARCHAR(20) NOT NULL);";
    /**
     * SQL Statement to create the games table
     */
    private static final String CREATE_GAMES_TABLE = "CREATE TABLE games(p1 INT, p2 INT, result INT NOT NULL, winner INT NOT NULL, started LONG NOT NULL, ended LONG, turn INT NOT NULL, CONSTRAINT fk_p1 FOREIGN KEY (p1) REFERENCES users(id), CONSTRAINT fk_p2 FOREIGN KEY (p2) REFERENCES users(id), CONSTRAINT fk_winner FOREIGN KEY (winner) REFERENCES users(id));";

    /**
     * SQL Statement to drop the users table
     */
    private static final String DROP_USERS_TABLE = "DROP TABLE IF EXISTS users;";
    /**
     * SQL Statement to drop the games table
     */
    private static final String DROP_GAMES_TABLE = "DROP TABLE IF EXISTS games;";

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
        log("Creating server on port: " + port + "...");

        log("Initializing database ocnnection...");
        dbConnection = DriverManager.getConnection("jdbc:h2:" + DB_PATH);
        if (!isDatabaseInitialized()) initDatabaseTables();

        try {
            listen();
            log("Listening for connections...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        initHeartbeatThread();
    }

    /**
     * Initializes the heartbeat thread that sends a heartbeat message to all clients every 60 seconds.
     */
    private void initHeartbeatThread() {
        log("Initializing heartbeat thread...");
        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!connections.isEmpty()) {
                    log("Sending hearbeat to " + connections.size() + " connections...");

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
     * @param event Event that was received from the client.
     * @param client Client from which the event originated.
     */
    private void handleEventFromClient(Event event, ConnectionToClient client) {
        User user = getUserForConnection(client);

        if (event instanceof ClientDisconnectEvent) {
            clientDisconnected(client);
        } else if (event instanceof LoginRequestEvent) {
            //TODO: Handle login request from client.
            //TODO: Send LoginSuccessEvent if successful, send LoginFailedEvent if bad login.
        }
    }

    /**
     * Callback method called each time a new client connects.
     *
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
        User user = getUserForConnection(client);
        if (user != null) user.setClient(null);

        logClient(client, "Disconnected");
        synchronized (connections) {
            connections.remove(client);
        }
    }

    /**
     * Callback method called when an exception is thrown for a client connection.
     *
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
     * @param msg Message being logged
     */
    private void logClient(ConnectionToClient client, Object msg) {
        log("[" + client + "]: " + msg);
    }

    /**
     * Logs a message.
     *
     * @param msg Message to be logged
     */
    private void log(String msg) {
        logger.info(msg);
    }

    /**
     * Retrieves a user object for a given client connection if the client connection is currently logged in to the system.
     *
     * @param client Client to match user with.
     * @return The user object that represents the client. Null if the client has not logged in yet.
     */
    private User getUserForConnection(ConnectionToClient client) {
        for (User user : users) {
            if (user.getClient() == client) return user;
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
        final int port = 54321;
        new Server(port);
    }

}
