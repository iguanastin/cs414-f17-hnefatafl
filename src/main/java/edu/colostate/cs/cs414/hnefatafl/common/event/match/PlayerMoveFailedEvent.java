package edu.colostate.cs.cs414.hnefatafl.common.event.match;


import edu.colostate.cs.cs414.hnefatafl.common.Event;

public class PlayerMoveFailedEvent extends Event {

    private final PlayerMoveFailedReason reason;


    public PlayerMoveFailedEvent(PlayerMoveFailedReason reason) {
        this.reason = reason;
    }

    public PlayerMoveFailedReason getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "Failed to make move. Reason: " + reason;
    }

}
