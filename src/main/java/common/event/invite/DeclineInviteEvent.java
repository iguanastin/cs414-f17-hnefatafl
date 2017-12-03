package common.event.invite;

import common.Event;
import common.UserID;


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
