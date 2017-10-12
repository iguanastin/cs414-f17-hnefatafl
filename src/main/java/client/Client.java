package client;


import common.ClientDisconnectEvent;
import common.ConnectAcceptedEvent;
import common.HeartbeatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Client extends AbstractClient {

    final Logger logger = LoggerFactory.getLogger(Client.class);


    public Client(String host, int port) throws IOException {
        super(host, port);
        logger.info("Connecting to server at " + host + ":" + port + "...");
        openConnection();
        sendToServer(new ConnectAcceptedEvent());
        logger.info("Connected!");
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        logger.info("[SERVER]: " + msg);

        if (msg instanceof HeartbeatEvent) {
            handleHeartbeat(msg);
        }
    }

    private void handleHeartbeat(Object msg) {
        try {
            sendToServer(msg);
        } catch (IOException e) {
            logger.error("Error sending response heartbeat to server", e);
            try {
                closeConnection();
            } catch (IOException ignored) {
            }
        }
    }

    public synchronized void disconnect() throws IOException {
        sendToServer(new ClientDisconnectEvent());
        closeConnection();
    }

    public static void main(String[] args) {
        try {
            new Client("localhost", 54321);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
