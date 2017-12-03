package client.ai;

import common.Invitation;
import common.UserID;
import common.event.invite.AcceptInviteEvent;
import common.event.invite.InviteAcceptedEvent;
import common.event.login.LoginRequestEvent;
import common.event.match.PlayerMoveEvent;
import common.game.Match;

import java.io.IOException;

import client.Client;
import client.InviteListener;
import client.MatchListener;
import common.game.MatchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AIClient extends Client implements MatchListener, InviteListener {
	//ID of the AI player
	private final Logger logger = LoggerFactory.getLogger(Client.class);

    public AIClient(String host, int port) throws IOException {
        super(host, port);

        //Login to server
        sendToServer(new LoginRequestEvent("AI", "Mr.Robot"));

        addInviteListener(this);
        addMatchListener(this);
    }

    @Override
    public void matchUpdated(Match match) {
        UserID enemy = match.getAttacker();
        boolean isDefender = true;
        if (enemy == getUserID()) {
            enemy = match.getDefender();
            isDefender = false;
        }
        if (isDefender && match.getStatus().equals(MatchStatus.DEFENDER_TURN) || (!(isDefender) && match.getStatus().equals(MatchStatus.ATTACKER_TURN)))
        {
            AI ai = new AI(match, getUserID(), isDefender);
            int move[] = ai.makeMove();
            try {
                sendToServer(new PlayerMoveEvent(enemy, move[0], move[1], move[2], move[3]));
            } catch (IOException e) {
                logger.error("Error sending player move to server", e);
            }
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
            sendToServer(new AcceptInviteEvent(invite.getSender()));
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
    @Override
    protected void connectionException(Exception exception) {
        try {
            throw exception;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}