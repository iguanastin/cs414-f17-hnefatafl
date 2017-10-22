package Game;

import org.junit.*;
import server.User;

public class BoardTest {
	User attacker;
	User defender;
	Board board;
	
	@Before public void initialize() {
		attacker = new User(1, "attacker", "", "");
		defender = new User(2, "defender", "", "");
		board = new Board(11,11);
	}
	@Test public void testInitializeTiles() {
		board.initializeTiles();
		Tile[][] tiles = board.getTiles();
		for(int i = 0; i < 11; i++) {
			for(int j = 0; j < 11; j++) {
				if (i == 5 && j == 5) {
					assert tiles[5][5].getType().equals(TileType.THRONE);
				}
				else if(i == 0 || i == 10 || j == 0 || j == 10) {
					assert tiles[i][j].getType().equals(TileType.GOAL);
				}
				else {
					assert tiles[i][j].getType().equals(TileType.NORMAL);
				}
			}
		}
	}
	@Test public void testInitializeAttack() {
		board.initializeTiles();
		board.initializeAttack(attacker);
		Tile[][] tiles = board.getTiles();
		//Check outer attackers
		for (int i = 3; i < 8; i++) {
			assert tiles[0][i].getPiece().getUser().equals(attacker);
			assert tiles[10][i].getPiece().getUser().equals(attacker);
			assert tiles[i][0].getPiece().getUser().equals(attacker);
			assert tiles[i][10].getPiece().getUser().equals(attacker);
		}
		//Check inner attackers
		assert tiles[1][5].getPiece().getUser().equals(attacker);
		assert tiles[9][5].getPiece().getUser().equals(attacker);
		assert tiles[5][1].getPiece().getUser().equals(attacker);
		assert tiles[5][9].getPiece().getUser().equals(attacker);
	}
	@Test public void testInitializeDefense() {
		board.initializeTiles();
		board.initializeDefense(defender);
		Tile[][] tiles = board.getTiles();
		//Check outer defenders
		assert tiles[3][5].getPiece().getUser().equals(defender);
		assert tiles[7][5].getPiece().getUser().equals(defender);
		assert tiles[5][3].getPiece().getUser().equals(defender);
		assert tiles[5][7].getPiece().getUser().equals(defender);
		//Check inner defenders
		for (int i = 4; i < 7; i++) {
			for (int j = 4; j < 7; j++) {
				assert tiles[i][j].getPiece().getUser().equals(defender);
			}
		}
		//Check King
		assert tiles[5][5].getPiece().getUser().equals(defender);
		assert tiles[5][5].getPiece().isKing();
	}
}
