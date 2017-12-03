package common.event.match;


import common.Event;

public class PlayerMoveEvent extends Event {

    private final int fromRow, fromCol, toRow, toCol, enemyId;


    public PlayerMoveEvent(int enemyId, int fromRow, int fromCol, int toRow, int toCol) {
        this.enemyId = enemyId;
        this.fromCol = fromCol;
        this.fromRow = fromRow;
        this.toCol = toCol;
        this.toRow = toRow;
    }

    public int getEnemyId() {
        return enemyId;
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
        return "Player requesting move from [" + fromRow + "," + fromCol + "] to [" + toRow + "," + toCol + "] against " + enemyId;
    }

}
