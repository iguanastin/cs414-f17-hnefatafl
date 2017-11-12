package common.event.invite;

import common.Event;
import common.Invitation;


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
        return "Received invite from: " + getInvite().getSenderID();
    }

}
