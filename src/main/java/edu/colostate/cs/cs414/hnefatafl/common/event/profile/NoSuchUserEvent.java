package edu.colostate.cs.cs414.hnefatafl.common.event.profile;


import edu.colostate.cs.cs414.hnefatafl.common.Event;

public class NoSuchUserEvent extends Event {

    private String username;


    public NoSuchUserEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "No such user: " + getUsername();
    }

}
