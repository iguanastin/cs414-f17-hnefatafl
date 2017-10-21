package Game;

import java.io.Serializable;

enum TileType implements Serializable {
    NORMAL, THRONE, GOAL
}

public class Tile implements Serializable {
    //type is NORMAL, THRONE, or GOAL
    private TileType type;
    //Tracks if this tile contains a piece
    private boolean hasPiece;
    //Contains piece placed on this tile
    private Piece piece;
    //Contains location of this tile
    private int x;
    private int y;

    public Tile(TileType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public boolean hasPiece() {
        return hasPiece;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        hasPiece = true;
    }

    public void removePiece() {
        this.piece = null;
        hasPiece = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
