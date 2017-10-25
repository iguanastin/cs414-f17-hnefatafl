package Game;

import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TileGUI extends BorderPane {

    private PieceGUI piece;
    private int xCoord, yCoord;

    // Create a tile in the appropriate space, and set color to gray
    public TileGUI(int x_coord, int y_coord) {
        this.xCoord = x_coord;
        this.yCoord = y_coord;

//        setWidth(GameGUIRunner.TILE_SIZE);
//        setHeight(GameGUIRunner.TILE_SIZE);
//
//        relocate(x_coord * GameGUIRunner.TILE_SIZE,
//                y_coord * GameGUIRunner.TILE_SIZE);
//        setFill(Color.GRAY);
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public PieceGUI getPiece() {
        return piece;
    }

    public void setPiece(PieceGUI p) {
        piece = p;
        setCenter(p);
    }

    public int getyCoord() {
        return yCoord;
    }

    public int getxCoord() {
        return xCoord;
    }

}

