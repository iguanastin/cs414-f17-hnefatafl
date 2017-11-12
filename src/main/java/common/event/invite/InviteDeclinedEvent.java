package common.event.invite;

import common.Event;
import common.Invitation;


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
