package common.event.invite;

import common.Event;


public class AcceptInviteEvent extends Event {

    private final int senderID;


    public AcceptInviteEvent(int senderID) {
        this.senderID = senderID;
    }

    public int getSenderID() {
        return senderID;
    }

    @Override
    public String toString() {
        return "Accepting invite from: " + senderID;
    }

}
