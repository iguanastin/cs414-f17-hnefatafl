package edu.colostate.cs.cs414.hnefatafl.client;


import edu.colostate.cs.cs414.hnefatafl.common.Invitation;

public interface InviteListener {

    void inviteReceived(Invitation invite);

    void inviteDeclined(Invitation invite);

    void inviteAccepted(Invitation invite);

}
