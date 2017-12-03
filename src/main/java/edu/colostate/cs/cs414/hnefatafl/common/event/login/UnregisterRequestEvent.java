package edu.colostate.cs.cs414.hnefatafl.common.event.login;

import edu.colostate.cs.cs414.hnefatafl.common.Event;


public class UnregisterRequestEvent extends Event {

    @Override
    public String toString() {
        return "Requesting account be unregistered";
    }

}
