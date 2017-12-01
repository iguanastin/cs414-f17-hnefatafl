package client;

import common.Invitation;
import common.event.invite.AcceptInviteEvent;
import common.event.invite.InviteAcceptedEvent;
import common.event.login.LoginRequestEvent;
import common.game.Match;

import java.io.IOException;


public class AIClient extends Client implements MatchListener, InviteListener {

    public AIClient(String host, int port) throws IOException {
        super(host, port);

        //Login to server
        sendToServer(new LoginRequestEvent("AI", "Mr.Robot"));

        addInviteListener(this);
        addMatchListener(this);
    }

    @Override
    public void matchUpdated(Match match) {
        //TODO: Handle AI logic and send a move request
        
    }

    @Override
    public void matchStarted(Match match) {
        //Shouldn't have to do anything
    }

    @Override
    public void matchFinished(Match match) {
        //Shouldn't have to do anything
    }

    @Override
    public void inviteReceived(Invitation invite) {
        //TODO: Send an AcceptInviteEvent immediately
        try {
            sendToServer(new AcceptInviteEvent(invite.getSenderID()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inviteDeclined(Invitation invite) {
        //Shouldn't have to do anything
    }

    @Override
    public void inviteAccepted(Invitation invite) {
        //Shouldn't have to do anything
    }

}