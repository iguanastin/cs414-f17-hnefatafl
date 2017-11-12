package common.event.login;

import common.Event;


public class UnregisterRequestEvent extends Event {

    @Override
    public String toString() {
        return "Requesting account be unregistered";
    }

}
