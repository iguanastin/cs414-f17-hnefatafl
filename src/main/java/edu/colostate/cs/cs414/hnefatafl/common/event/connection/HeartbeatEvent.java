package edu.colostate.cs.cs414.hnefatafl.common.event.connection;


import edu.colostate.cs.cs414.hnefatafl.common.Event;

public class HeartbeatEvent extends Event {

    @Override
    public String toString() {
        return "Received heartbeat";
    }

}
