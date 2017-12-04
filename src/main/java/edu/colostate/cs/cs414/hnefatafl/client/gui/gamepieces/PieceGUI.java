package edu.colostate.cs.cs414.hnefatafl.client.gui.gamepieces;

import edu.colostate.cs.cs414.hnefatafl.client.gui.GameTab;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class PieceGUI extends StackPane {

    private static final int TILE_SIZE = 75;

    private int xCoord, yCoord;

    private Circle circle;
    private PieceTypeGUI type;

    public PieceGUI(int x_coord, int y_coord, PieceTypeGUI type) {
        this.type = type;
        this.xCoord = x_coord;
        this.yCoord = y_coord;

        circle = new Circle(TILE_SIZE * 0.3);
        getChildren().add(circle);
        resetColor();
    }

    public void resetColor() {
        switch(type) {
            case ATTACKER:
                circle.setFill(GameTab.ATTACKER_COLOR);
                break;

            case DEFENDER:
                circle.setFill(GameTab.DEFENDER_COLOR);
                break;

            case KING:
                circle.setFill(GameTab.KING_COLOR);
                break;
        }
    }

    public PieceTypeGUI getType() {
        return type;
    }

    public Circle getCircle() {
        return circle;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

}

