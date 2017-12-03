package edu.colostate.cs.cs414.hnefatafl.common.event.invite;

import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.Invitation;


public class InviteDeclinedEvent extends Event {

    private final Invitation invite;


    public InviteDeclinedEvent(Invitation invite) {
        this.invite = invite;
    }

    public Invitation getInvite() {
        return invite;
    }

    @Override
    public String toString() {
        return "Invite declined for user: " + getInvite();
    }

}
