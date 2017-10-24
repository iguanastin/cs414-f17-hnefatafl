package common;


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
