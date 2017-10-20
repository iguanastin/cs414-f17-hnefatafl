package Game;

public class Board {
	//Contains all the board's tiles, which contain pieces.
	private Tile[][] tiles;
	//width and height parameters
	private int width;
	private int height;
	
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		tiles = new Tile[height][width];
	}
	public Tile[][] getTiles() {
		return tiles;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	//Can currently only initialize 11x11 boards
	public void initialize(User attacker, User defender) {
		if(width == 11 && height == 11) {
			//initialize tiles
				this.initializeTiles();
			//initialize pieces
				this.initializeAttack(attacker);
				this.initializeDefense(defender);
		}
	}
	public void initializeTiles() {
		for (int i = 0; i < 11; i++) {
			//Place the goal tiles
			tiles[0][i] = new Tile(TileType.GOAL, 0, i);
			tiles[10][i] = new Tile(TileType.GOAL, 10, i);
			tiles[i][0] = new Tile(TileType.GOAL, i, 0);
			tiles[i][10] = new Tile(TileType.GOAL, i , 10);
		}
		//Fill the remain space with normal tiles
		for (int i = 1; i < 10; i++) {
			for (int j = 1; j < 10; j++) {
				tiles[i][j] = new Tile(TileType.NORMAL, i, j);
			}
		}
		//Place the throne tile
		tiles[5][5] = new Tile(TileType.THRONE, 5, 5);
	}
	public void initializeAttack(User attacker) {
		//initialize outer attackers
		for (int i = 3; i < 8; i++) {
			tiles[0][i].setPiece(new Piece(attacker, Color.BLACK, false));
			tiles[10][i].setPiece(new Piece(attacker, Color.BLACK, false));
			tiles[i][0].setPiece(new Piece(attacker, Color.BLACK, false));
			tiles[i][10].setPiece(new Piece(attacker, Color.BLACK, false));
		}
		//initialize inner attackers
		tiles[1][5].setPiece(new Piece(attacker, Color.BLACK, false));
		tiles[9][5].setPiece(new Piece(attacker, Color.BLACK, false));
		tiles[5][1].setPiece(new Piece(attacker, Color.BLACK, false));
		tiles[5][9].setPiece(new Piece(attacker, Color.BLACK, false));
	}
	public void initializeDefense(User defender) {
		//initialize outer defenders
		tiles[3][5].setPiece(new Piece(defender, Color.WHITE, false));
		tiles[7][5].setPiece(new Piece(defender, Color.WHITE, false));
		tiles[5][3].setPiece(new Piece(defender, Color.WHITE, false));
		tiles[5][7].setPiece(new Piece(defender, Color.WHITE, false));
		//initialize inner defenders
		for (int i = 4; i < 7; i++) {
			for (int j = 4; j < 7; j++) {
				tiles[i][j].setPiece(new Piece(defender, Color.WHITE, false));
			}
		}
		//initialize king
		tiles[5][5].setPiece(new Piece(defender, Color.WHITE, true));
	}
	public String toString() {
		String toStr = "Tiles:\n";
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				toStr += tiles[j][i] + " ";
			}
			toStr += "\n";
		}
		toStr += "Pieces:\n";
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (tiles[j][i].hasPiece()) {
					if (tiles[j][i].getPiece().isKing()) {
						toStr += "K ";
					}
					else if (tiles[j][i].getPiece().getColor().equals(Color.BLACK)){
						toStr += "B ";
					}
					else {
						toStr += "W ";
					}
				}
				else {
					toStr += "  ";
				}
			}
			toStr += "\n";
		}
		return toStr;
	}
}