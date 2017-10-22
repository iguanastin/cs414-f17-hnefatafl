package client;


import common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Client extends AbstractClient {
    private boolean authenticated = false;

    /**
     * SLF$J Logger for logging info for this client
     */
    private final Logger logger = LoggerFactory.getLogger(Client.class);


    /**
     * Constructs a client and attempts to connect it to the given host:port
     *
     * @param host Hostname/IP of the server
     * @param port Port of the server
     * @throws IOException When the server does not exist on the given host:port
     */
    public Client(String host, int port) throws IOException {
        super(host, port);
        logger.info("Connecting to server at " + host + ":" + port + "...");
        openConnection();
        sendToServer(new ConnectAcceptedEvent());
        logger.info("Connected!");
    }

    /**
     * Central inlet for all messages received from the server. Called each time the server sends a message.
     *
     * @param msg the message sent.
     */
    @Override
    protected void handleMessageFromServer(Object msg) {
        logger.info("[SERVER]: " + msg);

        if (msg instanceof Event) handleEventFromServer((Event) msg);
    }

    /**
     * Central inlet for all events received from the server. Called each time the server send an event.
     *
     * @param event Event that the server sent.
     */
    private void handleEventFromServer(Event event) {
        if (event instanceof HeartbeatEvent) {
            handleHeartbeat(event);
        } else if (event instanceof LoginFailedEvent) {
            //TODO: Handle login attempt failed
            this.authenticated = false;
        } else if (event instanceof LoginSuccessEvent) {
            //TODO: Handle login attempt succeeded
            this.authenticated = true;
        }
    }

    /**
     * Sends the heartbeat event back to the server. If sending fails, disconnects the client.
     *
     * @param event HeartbeatEvent received from the server.
     */
    private void handleHeartbeat(Event event) {
        try {
            sendToServer(event);
        } catch (IOException e) {
            logger.error("Error sending response heartbeat to server", e);
            try {
                closeConnection();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Disconnects this client cleanly from the server.
     *
     * @throws IOException If the server is already disconnected.
     */
    public synchronized void disconnect() throws IOException {
        sendToServer(new ClientDisconnectEvent());
        closeConnection();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public static void main(String[] args) {
//        try {
//            ///new Client("localhost", 54321);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
