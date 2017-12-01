package client;

import common.Event;
import common.event.invite.InviteReceivedEvent;
import common.event.match.MatchUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class AI extends Client {
    private final ArrayList<LoginListener> loginListeners = new ArrayList<>();
    private final ArrayList<RegisterListener> registerListeners = new ArrayList<>();
    private final ArrayList<MatchListener> matchListeners = new ArrayList<>();
    private final ArrayList<ServerUtilListener> serverUtilListeners = new ArrayList<>();
    private final ArrayList<InviteListener> inviteListeners = new ArrayList<>();


    /**
     * SLF$J Logger for logging info for this client
     */
    private final Logger logger = LoggerFactory.getLogger(Client.class);


    /**
     * Constructs the client.
     *
     * @param host the server's host name.
     * @param port the port number.
     */
    public AI(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        logger.info("[SERVER]: " + msg);

        if (msg instanceof Event) handleEventFromServer((Event) msg);
    }

    private void handleEventFromServer(Event event) {
        if (event instanceof MatchUpdateEvent) {
            matchListeners.forEach(listener -> listener.matchUpdated(((MatchUpdateEvent) event).getMatch()));
        }else if (event instanceof InviteReceivedEvent) {
            inviteListeners.forEach(listener -> listener.inviteReceived(((InviteReceivedEvent) event).getInvite()));
        }
    }

    public void makeMove(){
        System.out.println("ASDASDASD");
    }
}
