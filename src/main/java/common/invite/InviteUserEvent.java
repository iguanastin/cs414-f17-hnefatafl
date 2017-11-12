package common.invite;

import common.Event;


public class InviteUserEvent extends Event {

    private final String username;


    public InviteUserEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "User is inviting user: " + getUsername();
    }

}
