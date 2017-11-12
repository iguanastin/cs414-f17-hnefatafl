package common.invite;

import common.Event;


public class InviteDeclinedEvent extends Event {

    private final String username;


    public InviteDeclinedEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "Invite declined for user: " + getUsername();
    }

}
