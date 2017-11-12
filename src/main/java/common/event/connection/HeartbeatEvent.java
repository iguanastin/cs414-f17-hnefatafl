package common.event.connection;


import common.Event;

public class HeartbeatEvent extends Event {

    @Override
    public String toString() {
        return "Received heartbeat";
    }

}
