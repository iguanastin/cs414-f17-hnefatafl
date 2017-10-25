package Game;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PieceGUI extends StackPane {

    private double mouseX, mouseY;
    private double oldX, oldY;
    private int xCoord, yCoord;

    public PieceGUI(int x_coord, int y_coord, PieceTypeGUI type) {
        movePiece(x_coord, y_coord);

        this.xCoord = x_coord;
        this.yCoord = y_coord;

        Circle piece = new Circle(GameGUIRunner.TILE_SIZE * 0.3);
        switch(type) {
            case ATTACKER:
                piece.setFill(Color.BLACK);
                break;

            case DEFENDER:
                piece.setFill(Color.WHITE);
                break;

            case KING:
                piece.setFill(Color.GOLD);
                break;
        }
        getChildren().add(piece);

        // Remember start location for mouse move
        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }

    public void movePiece(int x_coord, int y_coord) {
        oldX = x_coord * GameGUIRunner.TILE_SIZE;
        oldY = y_coord * GameGUIRunner.TILE_SIZE;
        relocate(oldX, oldY);
    }

    public double getOldY() { return oldY; }
    public double getOldX() { return oldX; }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

}

