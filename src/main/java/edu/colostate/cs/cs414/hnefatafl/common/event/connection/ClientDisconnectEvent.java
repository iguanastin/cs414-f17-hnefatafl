package edu.colostate.cs.cs414.hnefatafl.common.event.connection;


import edu.colostate.cs.cs414.hnefatafl.common.Event;

public class ClientDisconnectEvent extends Event {

    @Override
    public String toString() {
        return "Client is disconnecting";
    }

}
