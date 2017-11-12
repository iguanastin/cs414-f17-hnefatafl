package common.event.invite;

import common.Event;


public class DeclineInviteEvent extends Event {

    private final int senderID;


    public DeclineInviteEvent(int senderID) {
        this.senderID = senderID;
    }

    public int getSenderID() {
        return senderID;
    }

    @Override
    public String toString() {
        return "Declining invite from: " + getSenderID();
    }

}
