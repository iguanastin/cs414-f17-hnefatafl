package common.event.match;


import common.Event;
import common.UserID;

public class PlayerMoveEvent extends Event {

    private final int fromRow, fromCol, toRow, toCol;
    private final UserID enemy;


    public PlayerMoveEvent(UserID enemy, int fromRow, int fromCol, int toRow, int toCol) {
        this.enemy = enemy;
        this.fromCol = fromCol;
        this.fromRow = fromRow;
        this.toCol = toCol;
        this.toRow = toRow;
    }

    public UserID getEnemy() {
        return enemy;
    }

    public int getFromY() {
        return fromCol;
    }

    public int getFromX() {
        return fromRow;
    }

    public int getToY() {
        return toCol;
    }

    public int getToX() {
        return toRow;
    }

    @Override
    public String toString() {
        return "Player requesting move from [" + fromRow + "," + fromCol + "] to [" + toRow + "," + toCol + "] against " + enemy;
    }

}
