package common.connection;


import common.Event;

public class ConnectAcceptedEvent extends Event {

    @Override
    public String toString() {
        return "Connection accepted";
    }
}
