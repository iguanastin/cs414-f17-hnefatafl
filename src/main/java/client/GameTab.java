package client;

import Game.*;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Tab;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.Collections;


public class GameTab extends Tab {

    private Match match;
    private GridPane grid;

    private final ArrayList<MoveListener> moveListeners = new ArrayList<>();


    public GameTab(String title) {
        super(title);

        initGrid();
    }

    private void initGrid() {
        grid = new GridPane();
        grid.setGridLinesVisible(true);
        grid.getColumnConstraints().addAll(Collections.nCopies(11, new ColumnConstraints(50, 100, 1000, Priority.ALWAYS, HPos.CENTER, true)));
        grid.getRowConstraints().addAll(Collections.nCopies(11, new RowConstraints(50, 100, 1000, Priority.ALWAYS, VPos.CENTER, true)));
        setContent(grid);
    }

    public void setMatch(Match match) {
        this.match = match;

        updateMatchView();
    }

    public Match getMatch() {
        return match;
    }

    public void updateMatchView() {
        initGrid();
        Tile[][] tiles = match.getBoard().getTiles();

        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[0].length; col++) {
                TileGUI tileGUI = new TileGUI(row, col);
                tileGUI.setOnDragOver(event -> {
                    if (event.getGestureSource() instanceof PieceGUI) {
                        event.acceptTransferModes(TransferMode.ANY);
                    }
                    event.consume();
                });
                tileGUI.setOnDragDropped(event -> {
                    PieceGUI piece = (PieceGUI) event.getGestureSource();

                    moveListeners.forEach(listener -> listener.playerRequestedMove(match, piece.getyCoord(), piece.getxCoord(), tileGUI.getyCoord(), tileGUI.getxCoord()));
                });
                grid.add(tileGUI, col, row);

                Piece piece = tiles[col][row].getPiece();
                if (piece != null) {
                    PieceTypeGUI type = PieceTypeGUI.ATTACKER;
                    if (piece.isKing()) {
                        type = PieceTypeGUI.KING;
                    } else if (piece.getUser() == match.getDefender()) {
                        type = PieceTypeGUI.DEFENDER;
                    }

                    PieceGUI pieceGUI = new PieceGUI(row, col, type);
                    pieceGUI.setOnDragDetected(event -> {
                        Dragboard db = pieceGUI.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent cc = new ClipboardContent();
                        cc.putString(pieceGUI.toString());
                        db.setContent(cc);
                        event.consume();
                    });

                    tileGUI.setPiece(pieceGUI);
                }
            }
        }
    }

    public boolean addMoveListener(MoveListener listener) {
        return moveListeners.add(listener);
    }

    public boolean removeMoveListener(MoveListener listener) {
        return moveListeners.remove(listener);
    }

}
