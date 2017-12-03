package edu.colostate.cs.cs414.hnefatafl.common.event.invite;

import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.UserID;


public class AcceptInviteEvent extends Event {

    private final UserID sender;


    public AcceptInviteEvent(UserID sender) {
        this.sender = sender;
    }

    public UserID getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Accepting invite from: " + sender;
    }

}
