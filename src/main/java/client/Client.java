package client;


import common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class Client extends AbstractClient {

    private boolean authenticated = false;
    private int userID = -1;

    private final ArrayList<LoginListener> loginListeners = new ArrayList<>();
    private final ArrayList<MatchListener> matchListeners = new ArrayList<>();


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
            LoginFailedEvent lfEvent = (LoginFailedEvent) event;

            //Notify login listeners
            loginListeners.forEach(listener -> listener.loginFailed(lfEvent.getUsername()));
        } else if (event instanceof LoginSuccessEvent) {
            LoginSuccessEvent lsEvent = (LoginSuccessEvent) event;
            userID = lsEvent.getId();

            //Notify login listeners
            loginListeners.forEach(listener -> listener.loginSucceeded(lsEvent.getId(), lsEvent.getUsername()));
        } else if (event instanceof MatchStartEvent) {
            matchListeners.forEach(listener -> listener.matchStarted(((MatchStartEvent) event).getMatch()));
        } else if (event instanceof MatchUpdateEvent) {
            matchListeners.forEach(listener -> listener.matchUpdated(((MatchUpdateEvent) event).getMatch()));
        } else if (event instanceof MatchFinishEvent) {
            matchListeners.forEach(listener -> listener.matchFinished(((MatchFinishEvent) event).getMatch()));
        } else if (event instanceof PlayerMoveFailedEvent) {
            logger.error("Failed a player move");
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
        if (isConnected()) sendToServer(new ClientDisconnectEvent());
        closeConnection();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public boolean addLoginListener(LoginListener listener) {
        return loginListeners.add(listener);
    }

    public boolean addMatchListener(MatchListener listener) {
        return matchListeners.add(listener);
    }

    public boolean removeLoginListener(LoginListener listener) {
        return loginListeners.remove(listener);
    }

    public boolean removeMatchListener(MatchListener listener) {
        return matchListeners.remove(listener);
    }

    public int getUserID() {
        return userID;
    }

}
