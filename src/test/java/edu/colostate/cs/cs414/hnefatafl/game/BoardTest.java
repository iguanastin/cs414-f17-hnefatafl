package edu.colostate.cs.cs414.hnefatafl.game;

import edu.colostate.cs.cs414.hnefatafl.common.UserID;
import edu.colostate.cs.cs414.hnefatafl.common.game.Board;
import edu.colostate.cs.cs414.hnefatafl.common.game.Tile;
import edu.colostate.cs.cs414.hnefatafl.common.game.TileType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BoardTest {

	private UserID attacker;
	private UserID defender;
	private Board board;


	@Before
	public void initialize() {
		attacker = new UserID(1, "1");
		defender = new UserID(2, "2");
		board = new Board(11, 11);
	}

	@Test
	public void testInitializeTiles() {
		board.initializeTiles();
		Tile[][] tiles = board.getTiles();
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				if (i == 5 && j == 5) {
					assertEquals(TileType.THRONE, tiles[5][5].getType());
				} else if (i == 0 || i == 10 || j == 0 || j == 10) {
					assertEquals(TileType.GOAL, tiles[i][j].getType());
				} else {
					assertEquals(TileType.NORMAL, tiles[i][j].getType());
				}
			}
		}
	}

	@Test
	public void testInitializeAttack() {
		board.initializeTiles();
		board.initializeAttack(attacker);
		Tile[][] tiles = board.getTiles();
		//Check outer attackers
		for (int i = 3; i < 8; i++) {
			assertEquals(attacker, tiles[0][i].getPiece().getUser());
			assertEquals(attacker, tiles[10][i].getPiece().getUser());
			assertEquals(attacker, tiles[i][0].getPiece().getUser());
			assertEquals(attacker, tiles[i][10].getPiece().getUser());
		}
		//Check inner attackers
		assertEquals(attacker, tiles[1][5].getPiece().getUser());
		assertEquals(attacker, tiles[9][5].getPiece().getUser());
		assertEquals(attacker, tiles[5][1].getPiece().getUser());
		assertEquals(attacker, tiles[5][9].getPiece().getUser());
	}

	@Test
	public void testInitializeDefense() {
		board.initializeTiles();
		board.initializeDefense(defender);
		Tile[][] tiles = board.getTiles();
		//Check outer defenders
		assertEquals(defender, tiles[3][5].getPiece().getUser());
		assertEquals(defender, tiles[7][5].getPiece().getUser());
		assertEquals(defender, tiles[5][3].getPiece().getUser());
		assertEquals(defender, tiles[5][7].getPiece().getUser());
		//Check inner defenders
		for (int i = 4; i < 7; i++) {
			for (int j = 4; j < 7; j++) {
				assertEquals(defender, tiles[i][j].getPiece().getUser());
			}
		}
		//Check King
		assertEquals(defender, tiles[5][5].getPiece().getUser());
		assertTrue(tiles[5][5].getPiece().isKing());
	}
}