package client.ai;

import server.User;

public class Coordinate {
	private int x;
	private int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Coordinate && x == ((Coordinate) obj).getX() && y == ((Coordinate) obj).getY();
	}
	@Override
	public int hashCode() {
		return (x * 13) + (y * 11);
	}
}
