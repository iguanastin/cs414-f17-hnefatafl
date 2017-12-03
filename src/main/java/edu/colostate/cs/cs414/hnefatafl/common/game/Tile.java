package edu.colostate.cs.cs414.hnefatafl.common.game;

import java.io.Serializable;

public class Tile implements Serializable {
    //type is NORMAL, THRONE, or GOAL
    private TileType type;
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
        return piece != null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
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
