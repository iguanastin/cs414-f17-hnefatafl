package edu.colostate.cs.cs414.hnefatafl.common.event.invite;

import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.UserID;


public class AlreadyInvitedEvent extends Event {

    private UserID id;


    public AlreadyInvitedEvent(UserID id) {
        this.id = id;
    }

    public UserID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Invite already exists between you and " + getId();
    }

}
