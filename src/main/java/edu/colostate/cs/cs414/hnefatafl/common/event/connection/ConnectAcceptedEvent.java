package edu.colostate.cs.cs414.hnefatafl.common.event.connection;


import edu.colostate.cs.cs414.hnefatafl.common.Event;

public class ConnectAcceptedEvent extends Event {

    @Override
    public String toString() {
        return "Connection accepted";
    }
}
