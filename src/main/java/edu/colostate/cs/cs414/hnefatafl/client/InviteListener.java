package edu.colostate.cs.cs414.hnefatafl.client;


import edu.colostate.cs.cs414.hnefatafl.common.Invitation;
import edu.colostate.cs.cs414.hnefatafl.common.UserID;

public interface InviteListener {

    void inviteReceived(Invitation invite);

    void inviteDeclined(Invitation invite);

    void inviteAccepted(Invitation invite);

    void inviteAlreadyExists(UserID id);

    void alreadyInMatch(UserID id);

}
