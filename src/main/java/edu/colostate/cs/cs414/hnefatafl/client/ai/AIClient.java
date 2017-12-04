package edu.colostate.cs.cs414.hnefatafl.client.ai;

import edu.colostate.cs.cs414.hnefatafl.common.Invitation;
import edu.colostate.cs.cs414.hnefatafl.common.UserID;
import edu.colostate.cs.cs414.hnefatafl.common.event.invite.AcceptInviteEvent;
import edu.colostate.cs.cs414.hnefatafl.common.event.login.LoginRequestEvent;
import edu.colostate.cs.cs414.hnefatafl.common.event.match.PlayerMoveEvent;
import edu.colostate.cs.cs414.hnefatafl.common.game.Match;

import java.io.IOException;

import edu.colostate.cs.cs414.hnefatafl.client.Client;
import edu.colostate.cs.cs414.hnefatafl.client.InviteListener;
import edu.colostate.cs.cs414.hnefatafl.client.MatchListener;
import edu.colostate.cs.cs414.hnefatafl.common.game.MatchStatus;
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
    public void inviteAlreadyExists(UserID id) {
        //Shouldn't have to do anything
    }

    @Override
    public void alreadyInMatch(UserID id) {
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