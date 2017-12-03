package edu.colostate.cs.cs414.hnefatafl.client.gui;

import edu.colostate.cs.cs414.hnefatafl.common.UserID;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;


public class GameListCell extends ListCell<UserID> {

    private BorderPane pane;
    private Button leaveButton;
    private Label infoLabel;


    public GameListCell(GameListCellListener listener) {
        leaveButton = new Button("Leave");
        leaveButton.setOnAction(event -> listener.leaveClicked(getItem()));

        infoLabel = new Label("Match against: ...");
        pane = new BorderPane(infoLabel, null, leaveButton, null, null);
        setText(null);
    }

    @Override
    protected void updateItem(UserID item, boolean empty) {
        super.updateItem(item, empty);
        setEditable(false);
        if (item != null) {
            infoLabel.setText("Match against: " + item.getName());
            setGraphic(pane);
        } else {
            setGraphic(null);
        }
    }

}
