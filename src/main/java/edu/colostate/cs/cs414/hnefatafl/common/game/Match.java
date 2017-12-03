package edu.colostate.cs.cs414.hnefatafl.common.game;

import edu.colostate.cs.cs414.hnefatafl.common.UserID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class Match implements Serializable {
    private Board board;
    private UserID attacker;
    private UserID defender;
    private MatchStatus status;

    public Match(UserID attacker, UserID defender) {
        this.attacker = attacker;
        this.defender = defender;
        status = MatchStatus.ATTACKER_TURN;
        board = new Board(11, 11);
        board.initialize(attacker, defender);
    }

    public Match(UserID attacker, UserID defender, MatchStatus status, String board) {
        this.attacker = attacker;
        this.defender = defender;
        this.board = new Board(11, 11);
        this.board.initializeTiles();

        this.status = status;

        String[] work = board.split(",");

        for (int index = 0; index < work.length; index++) {
            int i = (int) Math.floor((double) index / (double) this.board.getWidth());
            int j = index % this.board.getWidth();

            UserID user = defender;
            if (work[index].equals("1")) user = attacker;

            Color color = Color.BLACK;
            if (user.equals(attacker)) color = Color.WHITE;

            boolean isKing = work[index].equals("3");

            this.board.getTiles()[i][j].setPiece(null);
            if (!work[index].equals("0")) {
                this.board.getTiles()[i][j].setPiece(new Piece(user, color, isKing));
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public UserID getAttacker() {
        return attacker;
    }

    public UserID getDefender() {
        return defender;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public boolean isOver() {
        return (!(status.equals(MatchStatus.ATTACKER_TURN) || status.equals(MatchStatus.DEFENDER_TURN)));
    }

    //Gets the player in control of this turn.
    public UserID getCurrentPlayer() {
        if (status.equals(MatchStatus.ATTACKER_TURN)) {
            return attacker;
        } else {
            return defender;
        }
    }

    //Swaps the player in control of this turn
    public void swapTurn() {
        if (status.equals(MatchStatus.ATTACKER_TURN)) {
            status = MatchStatus.DEFENDER_TURN;
        } else {
            status = MatchStatus.ATTACKER_TURN;
        }
    }

    /**
     * Gets available moves for a piece
     *
     * @param tile containing the piece to check available moves for
     * @return all available moves for the piece on the provided tile
     */
    public ArrayList<Tile> getAvaiableMoves(Tile tile) {
        ArrayList<Tile> availableMoves = new ArrayList<Tile>();
        //Confirm tile has a piece on it
        if (tile.hasPiece()) {
            int x = tile.getX();
            int y = tile.getY();
            Tile[][] tiles = board.getTiles();
            //Check areas left of tile
            if (x != 0) {
                for (int i = x - 1; i >= 0; i--) {
                    //Check if the tile has no piece on it and is not the throne
                    if (!(tiles[i][y].hasPiece()) && !(tiles[i][y].getType().equals(TileType.THRONE)))
                        availableMoves.add(tiles[i][y]);
                    else
                        break;
                }
            }
            //Check areas right of tile
            if (x != 10) {
                for (int i = x + 1; i <= 10; i++) {
                    //Check if the tile has no piece on it and is not the throne
                    if (!(tiles[i][y].hasPiece()) && !(tiles[i][y].getType().equals(TileType.THRONE)))
                        availableMoves.add(tiles[i][y]);
                    else
                        break;
                }
            }
            //Check above tile
            if (y != 0) {
                for (int i = y - 1; i >= 0; i--) {
                    //Check if the tile has no piece on it and is not the throne
                    if (!(tiles[x][i].hasPiece()) && !(tiles[x][i].getType().equals(TileType.THRONE)))
                        availableMoves.add(tiles[x][i]);
                    else
                        break;
                }
            }
            //Check below tile
            if (y != 10) {
                for (int i = y + 1; i <= 10; i++) {
                    //Check if the tile has no piece on it and is not the throne
                    if (!(tiles[x][i].hasPiece()) && !(tiles[x][i].getType().equals(TileType.THRONE)))
                        availableMoves.add(tiles[x][i]);
                    else
                        break;
                }
            }
        }
        return availableMoves;
    }

    // PRECONDITION: startTile contains a piece (no null checks needed)

    /**
     * Checks if a move is valid
     *
     * @param startTile the tile containing the piece to move
     * @param endTile   the tile for the piece to move to
     * @return true if the move can be made, false otherwise
     */
    public boolean isValidMove(Tile startTile, Tile endTile) {
        // Check to make sure not moving opponent's pieces
        if ((status == MatchStatus.ATTACKER_TURN) && (startTile.getPiece().getUser() != getAttacker())) {
            return false;
        } else if ((status == MatchStatus.DEFENDER_TURN) && (startTile.getPiece().getUser() != getDefender())) {
            return false;
        }

        // Check that the endTile was in the list of valid moves
        return getAvaiableMoves(startTile).contains(endTile);
    }

    /**
     * Checks if a move is valid
     *
     * @param x1 x coordinate containing piece
     * @param y1 y coordinate containing piece
     * @param x2 x coordinate to move piece to
     * @param y2 y coordinate to move piece to
     * @return true if the move can be made, false otherwise
     */
    public boolean isValidMove(int x1, int y1, int x2, int y2) {
        return isValidMove(getBoard().getTiles()[x1][y1], getBoard().getTiles()[x2][y2]);
    }

    //Moves the piece on fromTile to toTile, Returns a set of tiles who contained pieces captured by this move.

    /**
     * Makes a move
     *
     * @param fromTile the tile containing the piece to move
     * @param toTile   the tile for the piece to move to
     * @return any tiles that contain pieces that are captured by this move
     */
    public HashSet<Tile> makeMove(Tile fromTile, Tile toTile) {
        HashSet<Tile> capturedTiles = new HashSet<>();
        Piece toMove = fromTile.getPiece();
        fromTile.setPiece(null);
        toTile.setPiece(toMove);
        //Check if the king has moved to the goal
        if (toMove.isKing() && toTile.getType().equals(TileType.GOAL)) {
            status = MatchStatus.DEFENDER_WIN;
        }
        //If not king, capture pieces
        else if (!(toMove.isKing())) {
            //Captures any pieces available for capture by this move
            capturedTiles = capture(toTile);
        }
        return capturedTiles;
    }

    /**
     * Makes a move
     *
     * @param x1 x coordinate containing piece
     * @param y1 y coordinate containing piece
     * @param x2 x coordinate to move piece to
     * @param y2 y coordinate to move piece to
     * @return any tiles that contain pieces that are captured by this move
     */
    public HashSet<Tile> makeMove(int x1, int y1, int x2, int y2) {
        return makeMove(getBoard().getTiles()[x1][y1], getBoard().getTiles()[x2][y2]);
    }

    //Processes capturing performed by a piece, called by makeMove

    /**
     * Captures pieces using the piece on the provided tile
     *
     * @param capturerTile the tile containing the piece doing the capturing
     * @return the tiles containing pieces that are captured
     */
    private HashSet<Tile> capture(Tile capturerTile) {
        HashSet<Tile> capturedTiles = new HashSet<Tile>();
        int x = capturerTile.getX();
        int y = capturerTile.getY();
        Tile[][] tiles = board.getTiles();
        //Capture top piece if capturable
        if (y > 1) {
            if (aboveCapturable(capturerTile)) {
                //If piece to be captured is a king
                if (tiles[x][y - 1].getPiece().isKing()) {
                    //kingCapture attempts to capture the king.
                    if (kingCapture(tiles[x][y - 1])) {
                        //If king was successfully captured
                        capturedTiles.add(tiles[x][y - 1]);
                    }
                }
                //Capture the piece
                else {
                    capturedTiles.add(tiles[x][y - 1]);
                    tiles[x][y - 1].setPiece(null);
                }
            }
        }
        //Capture bottom piece if capturable
        if (y < 9) {
            if (belowCapturable(capturerTile)) {
                //If piece to be captured is a king
                if (tiles[x][y + 1].getPiece().isKing()) {
                    //kingCaptured attempts to capture the king.
                    if (kingCapture(tiles[x][y + 1])) {
                        //If king was successfully captured
                        capturedTiles.add(tiles[x][y + 1]);
                    }
                }
                //Capture the piece
                else {
                    capturedTiles.add(tiles[x][y + 1]);
                    tiles[x][y + 1].setPiece(null);
                }
            }
        }
        //Capture left piece if capturable
        if (x > 1) {
            if (leftCapturable(capturerTile)) {
                //If piece to be captured is a king
                if (tiles[x - 1][y].getPiece().isKing()) {
                    //kingCaptured attempts to capture the king.
                    if (kingCapture(tiles[x - 1][y])) {
                        //If king was successfully captured
                        capturedTiles.add(tiles[x - 1][y]);
                    }
                }
                //Capture the piece
                else {
                    capturedTiles.add(tiles[x - 1][y]);
                    tiles[x - 1][y].setPiece(null);
                }
            }
        }
        //Capture right piece if capturable
        if (x < 9) {
            if (rightCapturable(capturerTile)) {
                //If piece to be captured is a king
                if (tiles[x + 1][y].getPiece().isKing()) {
                    //kingCaptured attempts to capture the king.
                    if (kingCapture(tiles[x + 1][y])) {
                        //If king was successfully captured
                        capturedTiles.add(tiles[x + 1][y]);
                    }
                }
                //Capture the piece
                else {
                    capturedTiles.add(tiles[x + 1][y]);
                    tiles[x + 1][y].setPiece(null);
                }
            }
        }
        return capturedTiles;
    }

    /**
     * Checks if the piece above the provided tile is capturable
     *
     * @param capturerTile tile containing the piece doing the capturing
     * @return true if the piece above can be captured, false otherwise
     */
    private boolean aboveCapturable(Tile capturerTile) {
        Tile[][] tiles = board.getTiles();
        int x = capturerTile.getX();
        int y = capturerTile.getY();
        if (tiles[x][y - 1].hasPiece()) {
            //Check if top piece belongs to the enemy
            if (!(capturerTile.getPiece().getUser().equals(tiles[x][y - 1].getPiece().getUser()))) {
                //Check if there a piece on the other side of that piece
                if (tiles[x][y - 2].hasPiece()) {
                    //Check if that piece belongs to the capturer
                    if (capturerTile.getPiece().getUser().equals(tiles[x][y - 2].getPiece().getUser())) {
                        //Make sure that piece isn't a King
                        if (!(tiles[x][y - 2].getPiece().isKing())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the piece below the provided tile is capturable
     *
     * @param capturerTile tile containing the piece doing the capturing
     * @return true if the piece below can be captured, false otherwise
     */
    private boolean belowCapturable(Tile capturerTile) {
        Tile[][] tiles = board.getTiles();
        int x = capturerTile.getX();
        int y = capturerTile.getY();
        if (tiles[x][y + 1].hasPiece()) {
            //Check if top piece belongs to the enemy
            if (!(capturerTile.getPiece().getUser().equals(tiles[x][y + 1].getPiece().getUser()))) {
                //Check if there a piece on the other side of that piece
                if (tiles[x][y + 2].hasPiece()) {
                    //Check if that piece belongs to the capturer
                    if (capturerTile.getPiece().getUser().equals(tiles[x][y + 2].getPiece().getUser())) {
                        //Make sure that piece isn't a King
                        if (!(tiles[x][y + 2].getPiece().isKing())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the piece left of the provided tile is capturable
     *
     * @param capturerTile tile containing the piece doing the capturing
     * @return true if the left piece can be captured, false otherwise
     */
    private boolean leftCapturable(Tile capturerTile) {
        Tile[][] tiles = board.getTiles();
        int x = capturerTile.getX();
        int y = capturerTile.getY();
        if (tiles[x - 1][y].hasPiece()) {
            //Check if top piece belongs to the enemy
            if (!(capturerTile.getPiece().getUser().equals(tiles[x - 1][y].getPiece().getUser()))) {
                //Check if there a piece on the other side of that piece
                if (tiles[x - 2][y].hasPiece()) {
                    //Check if that piece belongs to the capturer
                    if (capturerTile.getPiece().getUser().equals(tiles[x - 2][y].getPiece().getUser())) {
                        //Make sure that piece isn't a King
                        if (!(tiles[x - 2][y].getPiece().isKing())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the piece to right of the provided tile is capturable
     *
     * @param capturerTile tile containing the piece doing the capturing
     * @return true if the right piece can be captured, false otherwise
     */
    private boolean rightCapturable(Tile capturerTile) {
        Tile[][] tiles = board.getTiles();
        int x = capturerTile.getX();
        int y = capturerTile.getY();
        if (tiles[x + 1][y].hasPiece()) {
            //Check if top piece belongs to the enemy
            if (!(capturerTile.getPiece().getUser().equals(tiles[x + 1][y].getPiece().getUser()))) {
                //Check if there a piece on the other side of that piece
                if (tiles[x + 2][y].hasPiece()) {
                    //Check if that piece belongs to the capturer
                    if (capturerTile.getPiece().getUser().equals(tiles[x + 2][y].getPiece().getUser())) {
                        //Make sure that piece isn't a King
                        if (!(tiles[x + 2][y].getPiece().isKing())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //Captures the king if surrounded.  Returns true is king was captured, false otherwise.

    /**
     * Captures the king if he is surrounded by enemy pieces and/or the throne.
     *
     * @param kingTile tile containing the king
     * @return true if the king is captured, false otherwise
     */
    private boolean kingCapture(Tile kingTile) {
        //If above, below, left, and right are all true, the king is surrounded and captured.
        boolean above = false;
        boolean below = false;
        boolean left = false;
        boolean right = false;
        int x = kingTile.getX();
        int y = kingTile.getY();
        //Check the tiles above, below, left and right
        if (y != 0) above = kingCheckAbove(kingTile);
        if (y != 10) below = kingCheckBelow(kingTile);
        if (x != 0) left = kingCheckLeft(kingTile);
        if (x != 10) right = kingCheckRight(kingTile);
        if (above && below && left && right) {
            kingTile.setPiece(null);
            status = MatchStatus.ATTACKER_WIN;
            return true;
        }
        return false;
    }

    /**
     * Checks if the piece/throne above the king can contribute to capturing it
     *
     * @param kingTile tile containing the king
     * @return true if the above piece/throne contributes to capturing the king, false otherwise
     */
    private boolean kingCheckAbove(Tile kingTile) {
        Tile[][] tiles = board.getTiles();
        int x = kingTile.getX();
        int y = kingTile.getY();
        //If it's the throne
        if (tiles[x][y - 1].getType().equals(TileType.THRONE)) {
            return true;
        }
        //Otherwise if a piece is there
        else if (tiles[x][y - 1].hasPiece()) {
            //Check if piece on tile belongs to the enemy
            if (!(kingTile.getPiece().getUser().equals(tiles[x][y - 1].getPiece().getUser()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the piece/throne below the king can contribute to capturing it
     *
     * @param kingTile tile containing the king
     * @return true if the below piece/throne contributes to capturing the king, false otherwise
     */
    private boolean kingCheckBelow(Tile kingTile) {
        Tile[][] tiles = board.getTiles();
        int x = kingTile.getX();
        int y = kingTile.getY();
        //If it's the throne
        if (tiles[x][y + 1].getType().equals(TileType.THRONE)) {
            return true;
        }
        //Otherwise if a piece is there
        else if (tiles[x][y + 1].hasPiece()) {
            //Check if piece on tile belongs to the enemy
            if (!(kingTile.getPiece().getUser().equals(tiles[x][y + 1].getPiece().getUser()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the piece/throne left of the king can contribute to capturing it
     *
     * @param kingTile tile containing the king
     * @return true if the left piece/throne contributes to capturing the king, false otherwise
     */
    private boolean kingCheckLeft(Tile kingTile) {
        Tile[][] tiles = board.getTiles();
        int x = kingTile.getX();
        int y = kingTile.getY();
        //If it's the throne
        if (tiles[x - 1][y].getType().equals(TileType.THRONE)) {
            return true;
        }
        //Otherwise if a piece is there
        else if (tiles[x - 1][y].hasPiece()) {
            //Check if piece on tile belongs to the enemy
            if (!(kingTile.getPiece().getUser().equals(tiles[x - 1][y].getPiece().getUser()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the piece/throne right of the king can contribute to capturing it
     *
     * @param kingTile tile containing the king
     * @return true if the right piece/throne contributes to capturing the king, false otherwise
     */
    private boolean kingCheckRight(Tile kingTile) {
        Tile[][] tiles = board.getTiles();
        int x = kingTile.getX();
        int y = kingTile.getY();
        //If it's the throne
        if (tiles[x + 1][y].getType().equals(TileType.THRONE)) {
            return true;
        }
        //Otherwise if a piece is there
        else if (tiles[x + 1][y].hasPiece()) {
            //Check if piece on tile belongs to the enemy
            if (!(kingTile.getPiece().getUser().equals(tiles[x + 1][y].getPiece().getUser()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Match && ((Match) obj).getAttacker().equals(getAttacker()) && ((Match) obj).getDefender().equals(getDefender());
    }

    @Override
    public int hashCode() {
        int hash = 17 * attacker.getId();
        hash = hash * 31 + defender.getId();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < getBoard().getHeight(); i++) {
            for (int j = 0; j < getBoard().getWidth(); j++) {
                if (sb.length() > 0) sb.append(",");
                Piece piece = getBoard().getTiles()[i][j].getPiece();
                if (piece == null) {
                    sb.append("0");
                } else if (piece.getUser().equals(getAttacker())) {
                    sb.append("1");
                } else if (piece.getUser().equals(getDefender())) {
                    if (piece.isKing()) {
                        sb.append("3");
                    } else {
                        sb.append("2");
                    }
                }
            }
        }

        return sb.toString();
    }

}
