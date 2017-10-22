package Game;

import java.util.ArrayList;

import org.junit.*;
import server.User;

public class MatchTest {
	private Match match;
	private User attacker;
	private User defender;
	private Board board;
	private Tile[][] tiles;
	
	private Tile[][] removeAllExceptKing(Tile[][] tiles){
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if(tiles[i][j].hasPiece()) {
					if(!(tiles[i][j].getPiece().isKing())) {
						tiles[i][j].removePiece();
					}
				}
			}
		}
		return tiles;
	}
	
	@Before public void initialize() {
		attacker = new User(1, "attacker", "", "");
		defender = new User(2, "defender", "", "");
		match = new Match(attacker, defender);
		board = match.getBoard();
		tiles = board.getTiles();
	}
	@Test public void testSwapTurn() {
		assert match.getStatus().equals(MatchStatus.ATTACKER_TURN);
		match.swapTurn();
		assert match.getStatus().equals(MatchStatus.DEFENDER_TURN);
		match.swapTurn();
		assert match.getStatus().equals(MatchStatus.ATTACKER_TURN);
	}
	@Test public void testGetCurrentPlayer() {
		assert match.getCurrentPlayer().equals(match.getAttacker());
		match.swapTurn();
		assert match.getCurrentPlayer().equals(match.getDefender());
	}
	@Test public void testGetAvailableMoves() {
		Tile[][] moves = tiles;
		removeAllExceptKing(moves);
		Tile kingTile = moves[5][5];
		ArrayList<Tile> kingMoves = match.getAvaiableMoves(kingTile);
		//Simple test
		assert kingMoves.toString().equals("[(4, 5), (3, 5), (2, 5), (1, 5), (0, 5), (6, 5), (7, 5), (8, 5), (9, 5), (10, 5), (5, 4), (5, 3), (5, 2), (5, 1), (5, 0), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10)]");
		//Move the King 1 space away from the throne
		moves[6][5].setPiece(kingTile.getPiece());
		kingTile.removePiece();
		//test throne logic
		kingMoves = match.getAvaiableMoves(moves[6][5]);
		assert kingMoves.toString().equals("[(7, 5), (8, 5), (9, 5), (10, 5), (6, 4), (6, 3), (6, 2), (6, 1), (6, 0), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10)]");
	}
	//Performs additional getAvailableMoves() testing, checks piece logic.
	@Test public void testGetAvailableMovesPieceLogic() {
		Tile testTile = tiles[5][3];
		assert match.getAvaiableMoves(testTile).toString().equals("[(4, 3), (3, 3), (2, 3), (1, 3), (6, 3), (7, 3), (8, 3), (9, 3), (5, 2)]");
	}
	@Test public void testMakeMove() {
		Tile testTile = tiles[5][3];
		assert testTile.hasPiece();
		assert !(tiles[5][2].hasPiece());
		match.makeMove(testTile, tiles[5][2]);
		assert !(testTile.hasPiece());
		assert tiles[5][2].hasPiece();
	}
	//Tests capturing against normal pieces
	@Test public void testCapture() {
		Tile[][] moves = tiles;
		removeAllExceptKing(moves);
		//Surround King with enemy pieces
		moves[4][5].setPiece(new Piece(attacker, Color.BLACK, false));
		moves[5][4].setPiece(new Piece(attacker, Color.BLACK, false));
		moves[6][5].setPiece(new Piece(attacker, Color.BLACK, false));
		moves[5][6].setPiece(new Piece(attacker, Color.BLACK, false));
		//Surround enemy pieces with friendly pieces
		moves[3][5].setPiece(new Piece(defender, Color.WHITE, false));
		moves[5][3].setPiece(new Piece(defender, Color.WHITE, false));
		moves[7][5].setPiece(new Piece(defender, Color.WHITE, false));
		moves[5][7].setPiece(new Piece(defender, Color.WHITE, false));
		//Perform captures performed by the king.  Done by moving the king piece to the spot it's already in.
		match.makeMove(moves[5][5], moves[5][5]);
		//Verify that enemy pieces are captured
		assert !(moves[4][5].hasPiece());
		assert !(moves[5][4].hasPiece());
		assert !(moves[6][5].hasPiece());
		assert !(moves[5][6].hasPiece());
		//Verify friendly pieces are still here
		assert defender.equals(moves[3][5].getPiece().getUser());
		assert defender.equals(moves[5][3].getPiece().getUser());
		assert defender.equals(moves[7][5].getPiece().getUser());
		assert defender.equals(moves[5][7].getPiece().getUser());
		//Verify king is still here
		assert moves[5][5].getPiece().isKing();
	}
	//Tests capturing against a king
	@Test public void testKingCapture() {
		Tile[][] moves = tiles;
		removeAllExceptKing(moves);
		//Surround King with enemy pieces
		moves[4][5].setPiece(new Piece(attacker, Color.BLACK, false));
		moves[5][4].setPiece(new Piece(attacker, Color.BLACK, false));
		moves[6][5].setPiece(new Piece(attacker, Color.BLACK, false));
		moves[5][6].setPiece(new Piece(attacker, Color.BLACK, false));
		//Capture the king with an attacker piece by moving it to the spot it's already at.
		match.makeMove(moves[4][5], moves[4][5]);
		assert !(moves[5][5].hasPiece());
	}
	@Test public void testAttackerWin() {
		Tile[][] moves = tiles;
		removeAllExceptKing(moves);
		//Surround King with enemy pieces
		moves[4][5].setPiece(new Piece(attacker, Color.BLACK, false));
		moves[5][4].setPiece(new Piece(attacker, Color.BLACK, false));
		moves[6][5].setPiece(new Piece(attacker, Color.BLACK, false));
		moves[5][6].setPiece(new Piece(attacker, Color.BLACK, false));
		//Capture the king with an attacker piece by moving it to the spot it's already at.
		assert match.getStatus().equals(MatchStatus.ATTACKER_TURN);
		match.makeMove(moves[4][5], moves[4][5]);
		assert match.getStatus().equals(MatchStatus.ATTACKER_WIN);
	}
	@Test public void testDefenderWin() {
		Tile[][] moves = tiles;
		//Swap to defender turn
		match.swapTurn();
		removeAllExceptKing(moves);
		assert match.getStatus().equals(MatchStatus.DEFENDER_TURN);
		match.makeMove(moves[5][5], moves[5][10]);
		assert match.getStatus().equals(MatchStatus.DEFENDER_WIN);
	}
}
