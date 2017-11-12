package common.invite;

import common.Event;


public class InviteAcceptedEvent extends Event {

    private final String username;


    public InviteAcceptedEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "Invite accepted by user: " + getUsername();
    }

}
