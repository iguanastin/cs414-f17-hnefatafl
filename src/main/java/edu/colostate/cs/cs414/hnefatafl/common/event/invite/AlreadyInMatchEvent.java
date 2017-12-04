package edu.colostate.cs.cs414.hnefatafl.common.event.invite;

import edu.colostate.cs.cs414.hnefatafl.common.UserID;


public class AlreadyInMatchEvent extends AlreadyInvitedEvent {

    public AlreadyInMatchEvent(UserID id) {
        super(id);
    }

    @Override
    public String toString() {
        return "User is already in a match with you: " + getId();
    }
}
