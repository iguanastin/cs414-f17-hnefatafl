package edu.colostate.cs.cs414.hnefatafl.common.event.profile;


import edu.colostate.cs.cs414.hnefatafl.common.Event;

public class RequestProfileEvent extends Event {

    private final String username;


    public RequestProfileEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "Requesting game history of user: " + getUsername();
    }

}
