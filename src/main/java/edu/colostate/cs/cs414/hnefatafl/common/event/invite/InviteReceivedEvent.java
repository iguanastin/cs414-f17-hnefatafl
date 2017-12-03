package edu.colostate.cs.cs414.hnefatafl.common.event.invite;

import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.Invitation;


public class InviteReceivedEvent extends Event {

    private final Invitation invite;


    public InviteReceivedEvent(Invitation invite) {
        this.invite = invite;
    }

    public Invitation getInvite() {
        return invite;
    }

    @Override
    public String toString() {
        return "Received invite from: " + getInvite().getSender();
    }

}
