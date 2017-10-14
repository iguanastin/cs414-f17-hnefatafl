package Game;

public class Tile {
	//type is NORMAL, THRONE, or GOAL
	private TileType type;
	//Tracks if this tile contains a piece
	private boolean hasPiece;
	//Contains piece placed on this tile
	private Piece piece;
	
	public Tile(TileType type) {
		this.type = type;
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
	public String toString() {
		return "" + type;
	}
}
