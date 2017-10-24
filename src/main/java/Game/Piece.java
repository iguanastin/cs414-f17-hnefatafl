package Game;

import java.io.Serializable;

enum Color implements Serializable {
    BLACK, WHITE
}

public class Piece implements Serializable {
    //Contains owner of piece
    private int user;
    private boolean isKing;
    private Color color;

    public Piece(int user, Color color, boolean isKing) {
        this.user = user;
        this.isKing = isKing;
        this.color = color;
    }

    public int getUser() {
        return user;
    }

    public boolean isKing() {
        return isKing;
    }

    public Color getColor() {
        return color;
    }

    public String toString() {
        return "isKing:" + isKing + " Color:" + color;
    }
}
