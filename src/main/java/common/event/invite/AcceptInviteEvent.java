package common.event.invite;

import common.Event;
import common.UserID;


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
