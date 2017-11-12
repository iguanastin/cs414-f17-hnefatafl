package common.connection;


import common.Event;

public class ClientDisconnectEvent extends Event {

    @Override
    public String toString() {
        return "Client is disconnecting";
    }

}
