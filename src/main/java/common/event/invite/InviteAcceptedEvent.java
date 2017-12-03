package common.event.invite;

import common.Event;
import common.Invitation;


public class InviteAcceptedEvent extends Event {

    private final Invitation invite;


    public InviteAcceptedEvent(Invitation invite) {
        this.invite = invite;
    }

    public Invitation getInvite() {
        return invite;
    }

    @Override
    public String toString() {
        return "Invite accepted by user: " + invite.getTarget();
    }

}
