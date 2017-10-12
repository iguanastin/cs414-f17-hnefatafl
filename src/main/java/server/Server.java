package server;

import common.ClientDisconnectEvent;
import common.ConnectAcceptedEvent;
import common.HeartbeatEvent;
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

    private static final String CREATE_USERS_TABLE = "CREATE TABLE users(id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, name NVARCHAR(20) NOT NULL UNIQUE, pass NVARCHAR(20) NOT NULL);";
    private static final String CREATE_GAMES_TABLE = "CREATE TABLE games(p1 INT, p2 INT, result INT NOT NULL, winner INT NOT NULL, started LONG NOT NULL, ended LONG, turn INT NOT NULL, CONSTRAINT fk_p1 FOREIGN KEY (p1) REFERENCES users(id), CONSTRAINT fk_p2 FOREIGN KEY (p2) REFERENCES users(id), CONSTRAINT fk_winner FOREIGN KEY (winner) REFERENCES users(id));";

    private static final String DROP_USERS_TABLE = "DROP TABLE IF EXISTS users;";
    private static final String DROP_GAMES_TABLE = "DROP TABLE IF EXISTS past_games;";

    private Connection dbConnection;
    private static final String DB_PATH = "./hnefatafl";

    private final ArrayList<ConnectionToClient> connections = new ArrayList<>();

    final Logger logger = LoggerFactory.getLogger(Server.class);


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

    private boolean isDatabaseInitialized() {
        try (Statement s = dbConnection.createStatement()) {
            s.executeQuery("SELECT * FROM users;");
            s.executeQuery("SELECT * FROM games;");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void initDatabaseTables() throws SQLException {
        Statement s = dbConnection.createStatement();

        s.executeUpdate(DROP_USERS_TABLE);
        s.executeUpdate(CREATE_USERS_TABLE);

        s.executeUpdate(DROP_GAMES_TABLE);
        s.executeUpdate(CREATE_GAMES_TABLE);

        s.close();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        logClient(client, msg);

        if (msg instanceof ClientDisconnectEvent) {
            clientDisconnected(client);
        }
    }

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

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        logClient(client, "Disconnected");
        synchronized (connections) {
            connections.remove(client);
        }
    }

    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        logClient(client, "Threw exception: " + exception.getLocalizedMessage());
        clientDisconnected(client);
    }

    private void logClient(ConnectionToClient client, Object msg) {
        log("[" + client + "]: " + msg);
    }

    private void log(String msg) {
        logger.info(msg);
    }

    public static void main(String[] args) throws SQLException {
        final int port = 54321;
        new Server(port);
    }

}
