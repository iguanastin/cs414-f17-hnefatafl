package client.gui;

import common.UserID;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


public class InviteListCell extends ListCell<UserID> {

    private HBox pane;
    private Label infoLabel;


    public InviteListCell(InviteListCellListener listener) {
        Button acceptButton = new Button("Accept");
        acceptButton.setOnAction(event -> listener.acceptClicked(getItem()));
        Button declineButton = new Button("Decline");
        declineButton.setOnAction(event -> listener.declineClicked(getItem()));

        infoLabel = new Label("Invitation from: ...");
        HBox.setHgrow(infoLabel, Priority.ALWAYS);
        pane = new HBox(5, infoLabel, acceptButton, declineButton);
    }

    @Override
    protected void updateItem(UserID item, boolean empty) {
        super.updateItem(item, empty);
        setEditable(false);
        if (item != null) {
            infoLabel.setText("Invitation from: " + item.getName());
            setGraphic(pane);
        } else {
            setGraphic(null);
        }
    }
}
