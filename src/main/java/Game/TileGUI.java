package Game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TileGUI extends Rectangle {

    private PieceGUI piece;

    // Create a tile in the appropriate space, and set color to gray
    public TileGUI(int x_coord, int y_coord) {
        setWidth(GameGUIRunner.TILE_SIZE);
        setHeight(GameGUIRunner.TILE_SIZE);

        relocate(x_coord * GameGUIRunner.TILE_SIZE,
                y_coord * GameGUIRunner.TILE_SIZE);
        setFill(Color.GRAY);
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public PieceGUI getPiece() {
        return piece;
    }

    public void setPiece(PieceGUI p) {
        piece = p;
    }
}

