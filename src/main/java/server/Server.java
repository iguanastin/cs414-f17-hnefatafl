package server;

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


    public Server(int port) throws SQLException {
        super(port);

        dbConnection = DriverManager.getConnection("jdbc:h2:" + DB_PATH);
        if (!isDatabaseInitialized()) initDatabaseTables();

        try {
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Timer(true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (Thread thread : getClientConnections()) {
                    try {
                        //TODO: Implement heartbeat event sending
//                        ((ConnectionToClient) thread).sendToClient(new HeartbeatEvent());
                        ((ConnectionToClient) thread).sendToClient("hearbeat");
                    } catch (IOException e) {
                        try {
                            ((ConnectionToClient) thread).close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
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

    public static void main(String[] args) throws SQLException {
        Server server = new Server(45454);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println(msg);
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println(client.getInetAddress().getHostAddress() + ": connected");
        connections.add(client);
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        System.out.println(client.getInetAddress().getHostAddress() + ": disconnected");
        connections.remove(client);
    }

    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        System.out.println(client.getInetAddress().getHostAddress() + ": threw exception");
        exception.printStackTrace();
    }

}
