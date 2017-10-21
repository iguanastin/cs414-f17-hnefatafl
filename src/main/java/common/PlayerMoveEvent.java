package common;


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

    public int getFromCol() {
        return fromCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getToCol() {
        return toCol;
    }

    public int getToRow() {
        return toRow;
    }

    @Override
    public String toString() {
        return "Player requesting move from [" + fromRow + "," + fromCol + "] to [" + toRow + "," + toCol + "] against " + enemyId;
    }

}
