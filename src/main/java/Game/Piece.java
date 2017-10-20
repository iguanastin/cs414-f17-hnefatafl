package Game;

import server.User;

enum Color {
	BLACK, WHITE
}
public class Piece {
	//Contains owner of piece
	private User user;
	private boolean isKing;
	private Color color;
	
	public Piece(User user, Color color, boolean isKing) {
		this.user = user;
		this.isKing = isKing;
		this.color = color;
	}
	public User getUser() {
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
