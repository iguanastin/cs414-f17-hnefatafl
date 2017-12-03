package edu.colostate.cs.cs414.hnefatafl.common.game;

import edu.colostate.cs.cs414.hnefatafl.common.UserID;

import java.io.Serializable;

public class Piece implements Serializable {
    //Contains owner of piece
    private UserID user;
    private boolean isKing;
    private Color color;

    public Piece(UserID user, Color color, boolean isKing) {
        this.user = user;
        this.isKing = isKing;
        this.color = color;
    }

    public UserID getUser() {
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
