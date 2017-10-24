package Game;

import java.util.ArrayList;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MatchTest {

    private Match match;
    private int attacker;
    private int defender;
    private Board board;
    private Tile[][] tiles;


    private Tile[][] removeAllExceptKing(Tile[][] tiles) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j].hasPiece()) {
                    if (!(tiles[i][j].getPiece().isKing())) {
                        tiles[i][j].removePiece();
                    }
                }
            }
        }
        return tiles;
    }

    @Before
    public void initialize() {
        attacker = 1;
        defender = 2;
        match = new Match(attacker, defender);
        board = match.getBoard();
        tiles = board.getTiles();
    }

    @Test
    public void testSwapTurn() {
        assertEquals(MatchStatus.ATTACKER_TURN, match.getStatus());
        match.swapTurn();
        assertEquals(MatchStatus.DEFENDER_TURN, match.getStatus());
        match.swapTurn();
        assertEquals(MatchStatus.ATTACKER_TURN, match.getStatus());
    }

    @Test
    public void testGetCurrentPlayer() {
        assertEquals(match.getAttacker(), match.getCurrentPlayer());
        match.swapTurn();
        assertEquals(match.getDefender(), match.getCurrentPlayer());
    }

    @Test
    public void testGetAvailableMoves() {
        Tile[][] moves = tiles;
        removeAllExceptKing(moves);
        Tile kingTile = moves[5][5];
        ArrayList<Tile> kingMoves = match.getAvaiableMoves(kingTile);
        //Simple test
        assertEquals("[(4, 5), (3, 5), (2, 5), (1, 5), (0, 5), (6, 5), (7, 5), (8, 5), (9, 5), (10, 5), (5, 4), (5, 3), (5, 2), (5, 1), (5, 0), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10)]", kingMoves.toString());
        //Move the King 1 space away from the throne
        moves[6][5].setPiece(kingTile.getPiece());
        kingTile.removePiece();
        //test throne logic
        kingMoves = match.getAvaiableMoves(moves[6][5]);
        assertEquals("[(7, 5), (8, 5), (9, 5), (10, 5), (6, 4), (6, 3), (6, 2), (6, 1), (6, 0), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10)]", kingMoves.toString());
    }

    //Performs additional getAvailableMoves() testing, checks piece logic.
    @Test
    public void testGetAvailableMovesPieceLogic() {
        Tile testTile = tiles[5][3];
        assertEquals("[(4, 3), (3, 3), (2, 3), (1, 3), (6, 3), (7, 3), (8, 3), (9, 3), (5, 2)]", match.getAvaiableMoves(testTile).toString());
    }

    @Test
    public void testMakeMove() {
        Tile testTile = tiles[5][3];
        assert testTile.hasPiece();
        assert !(tiles[5][2].hasPiece());
        match.makeMove(testTile, tiles[5][2]);
        assert !(testTile.hasPiece());
        assert tiles[5][2].hasPiece();
    }

    //Tests capturing against normal pieces
    @Test
    public void testCapture() {
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
        assertFalse(moves[4][5].hasPiece());
        assertFalse(moves[5][4].hasPiece());
        assertFalse(moves[6][5].hasPiece());
        assertFalse(moves[5][6].hasPiece());
        //Verify friendly pieces are still here
        assertEquals(defender, moves[3][5].getPiece().getUser());
        assertEquals(defender, moves[5][3].getPiece().getUser());
        assertEquals(defender, moves[7][5].getPiece().getUser());
        assertEquals(defender, moves[5][7].getPiece().getUser());
        //Verify king is still here
        assertTrue(moves[5][5].getPiece().isKing());
    }

    //Tests capturing against a king
    @Test
    public void testKingCapture() {
        Tile[][] moves = tiles;
        removeAllExceptKing(moves);
        //Surround King with enemy pieces
        moves[4][5].setPiece(new Piece(attacker, Color.BLACK, false));
        moves[5][4].setPiece(new Piece(attacker, Color.BLACK, false));
        moves[6][5].setPiece(new Piece(attacker, Color.BLACK, false));
        moves[5][6].setPiece(new Piece(attacker, Color.BLACK, false));
        //Capture the king with an attacker piece by moving it to the spot it's already at.
        match.makeMove(moves[4][5], moves[4][5]);
        assertFalse(moves[5][5].hasPiece());
    }

    @Test
    public void testAttackerWin() {
        Tile[][] moves = tiles;
        removeAllExceptKing(moves);
        //Surround King with enemy pieces
        moves[4][5].setPiece(new Piece(attacker, Color.BLACK, false));
        moves[5][4].setPiece(new Piece(attacker, Color.BLACK, false));
        moves[6][5].setPiece(new Piece(attacker, Color.BLACK, false));
        moves[5][6].setPiece(new Piece(attacker, Color.BLACK, false));
        //Capture the king with an attacker piece by moving it to the spot it's already at.
        assertEquals(MatchStatus.ATTACKER_TURN, match.getStatus());
        match.makeMove(moves[4][5], moves[4][5]);
        assertEquals(MatchStatus.ATTACKER_WIN, match.getStatus());
    }

    @Test
    public void testDefenderWin() {
        Tile[][] moves = tiles;
        //Swap to defender turn
        match.swapTurn();
        removeAllExceptKing(moves);
        assertEquals(MatchStatus.DEFENDER_TURN, match.getStatus());
        match.makeMove(moves[5][5], moves[5][10]);
        assertEquals(MatchStatus.DEFENDER_WIN, match.getStatus());
    }
}
