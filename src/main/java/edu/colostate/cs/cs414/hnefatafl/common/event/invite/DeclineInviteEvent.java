package edu.colostate.cs.cs414.hnefatafl.common.event.invite;

import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.UserID;


public class DeclineInviteEvent extends Event {

    private final UserID sender;


    public DeclineInviteEvent(UserID sender) {
        this.sender = sender;
    }

    public UserID getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Declining invite from: " + getSender();
    }

}
