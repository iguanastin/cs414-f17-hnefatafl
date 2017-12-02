package client.ai;

import common.Invitation;
import common.event.invite.AcceptInviteEvent;
import common.event.invite.InviteAcceptedEvent;
import common.event.login.LoginRequestEvent;
import common.event.match.PlayerMoveEvent;
import common.game.Match;

import java.io.IOException;

import client.Client;
import client.InviteListener;
import client.MatchListener;


public class AIClient extends Client implements MatchListener, InviteListener {
	//ID of the AI player
	static int AIid = 33;

    public AIClient(String host, int port) throws IOException {
        super(host, port);

        //Login to server
        sendToServer(new LoginRequestEvent("AI", "Mr.Robot"));

        addInviteListener(this);
        addMatchListener(this);
    }

    @Override
    public void matchUpdated(Match match) {
    	int enemyID = match.getAttacker();
        if (enemyID == AIid) enemyID = match.getDefender();
        int move[] = AI.makeMove(match, AIid);
    	try {
            sendToServer(new PlayerMoveEvent(enemyID, move[0], move[1], move[2], move[3]));
        } catch (IOException e) {
            logger.error("Error sending player move to server", e);
        }
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