package client;


import common.Invitation;

public interface InviteListener {

    void inviteReceived(Invitation invite);

    void inviteDeclined(Invitation invite);

    void inviteAccepted(Invitation invite);

}
