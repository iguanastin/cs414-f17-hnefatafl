package Game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.stage.Stage;
import java.util.HashSet;
import server.User;

// AUTHOR: Cole Juracek
// Credit to "JavaFX Game: Checkers" (https://www.youtube.com/watch?v=6S6km5duBrM) for guidance/structure.
// Some amount of code taken and modified for Hnefetafl.
public class GameGUIRunner extends Application{

    public static final int TILE_SIZE = 75;
    public static final int NUM_ROWS = 11, NUM_COLS = 11;

    private Pane root = new Pane();
    private TileGUI[][] board = new TileGUI[NUM_ROWS][NUM_COLS];
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();

    private Match match = new Match(1, 2);

    private Parent createContent() {
        root.setPrefSize(NUM_ROWS * TILE_SIZE, NUM_COLS * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);

        setUpBoard();
        drawLines();

        return root;
    }

    private void setUpBoard() {
        for(int y = 0; y < NUM_ROWS; y++) {
            for(int x = 0; x < NUM_COLS; x++) {
                TileGUI tile = new TileGUI(x, y);
                board[x][y] = tile;
                tileGroup.getChildren().add(tile);

                PieceGUI piece = null;

                // * NOTE * : Presumably a better way to do this; will save refactoring for later
                // Attacker locations
                if((x == 0 && y == 3) || (x == 0 && y == 4) || (x == 0 && y == 5) || (x == 0 && y == 6) || (x == 0 && y == 7)
                        || (x == 10 && y == 3) || (x == 10 && y == 4) || (x == 10 && y == 5) || (x == 10 && y == 6) || (x == 10 && y == 7)
                        || (y == 0 && x == 3) || (y == 0 && x == 4) || (y == 0 && x == 5) || (y == 0 && x == 6) || (y == 0 && x == 7)
                        || (y == 10 && x == 3) || (y == 10 && x == 4) || (y == 10 && x == 5) || (y == 10 && x == 6) || (y == 10 && x == 7)
                        || (x == 1 && y == 5) || (x == 9 && y == 5) || (x == 5 && y == 1) || (x == 5 && y == 9)) {
                    piece = makePiece(PieceTypeGUI.ATTACKER, x, y);
                }
                // Defender locations
                else if((x == 3 && y == 5) || (x == 4 && y == 4) || (x == 4 && y == 5) || (x == 4 && y == 6)
                        || (x == 5 && y == 3) || (x == 5 && y == 4) || (x == 5 && y == 6) || (x == 5 && y == 7)
                        || (x == 6 && y == 4) || (x == 6 && y == 5) || (x == 6 && y == 6)
                        || (x == 7 && y == 5)) {
                    piece = makePiece(PieceTypeGUI.DEFENDER, x, y);
                }
                // King location
                else if((x == 5 && y == 5)) {
                    piece = makePiece(PieceTypeGUI.KING, x, y);
                }

                if(piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
    }

    private PieceGUI makePiece(PieceTypeGUI type, int x_coord, int y_coord) {
        PieceGUI piece = new PieceGUI(x_coord, y_coord, type);

        // Piece was release from drag; attempt a move
        piece.setOnMouseReleased(e -> {
            int oldX_board = toBoard(piece.getOldX());
            int oldY_board = toBoard(piece.getOldY());
            int newX_board = toBoard(piece.getLayoutX());
            int newY_board = toBoard(piece.getLayoutY());

            makeMove(piece, oldX_board, oldY_board, newX_board, newY_board);
        });

        return piece;
    }

    // Converts a pixel coordinate to board (array) coordinate
    private int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    // Makes a move, updating the model and view accordingly
    private void makeMove(PieceGUI piece, int oldX_board, int oldY_board, int newX_board, int newY_board) {

        // Make move in model, and update view's board
        HashSet<Tile> capturedTiles = match.makeMove(match.getBoard().getTiles()[oldX_board][oldY_board],
                match.getBoard().getTiles()[newX_board][newY_board]);
        board[oldX_board][oldY_board].setPiece(null);
        board[newX_board][newY_board].setPiece(piece);
        piece.movePiece(newX_board, newY_board);

        // Check to see if game is over
        if(match.getStatus() == MatchStatus.ATTACKER_WIN) {
            System.out.println("ATTACKER WIN");
        }
        else if(match.getStatus() == MatchStatus.DEFENDER_WIN) {
            System.out.println("DEFENDER WIN");
        }

        // Remove captured pieces from GUI (anywhere from 0-3)
        for(Tile tile : capturedTiles) {
            pieceGroup.getChildren().remove(board[tile.getX()][tile.getY()].getPiece());
            board[tile.getX()][tile.getY()].setPiece(null);
        }

        match.swapTurn();
    }

    // Draw grid lines for board
    private void drawLines() {
        // Draw vertical lines
        for(int x = 1; x < NUM_COLS; x++) {
            Line line = LineBuilder.create()
                    .startX(x * TILE_SIZE)
                    .startY(0)
                    .endX(x * TILE_SIZE)
                    .endY(NUM_COLS * TILE_SIZE)
                    .fill(Color.BLACK)
                    .strokeWidth(5.0f)
                    .build();
            root.getChildren().add(line);
        }

        // Draw horizontal lines
        for(int y = 1; y < NUM_ROWS; y++) {
            Line line = LineBuilder.create()
                    .startX(0)
                    .startY(y * TILE_SIZE)
                    .endX(NUM_ROWS * TILE_SIZE)
                    .endY(y * TILE_SIZE)
                    .fill(Color.BLACK)
                    .strokeWidth(5.0f)
                    .build();
            root.getChildren().add(line);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Hnefetafl");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

