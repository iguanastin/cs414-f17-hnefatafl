package edu.colostate.cs.cs414.hnefatafl.common.event.invite;

import edu.colostate.cs.cs414.hnefatafl.common.Event;


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
