package client;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ClientController {

    public ListView<String> invitesListView;
    public ListView<String> gamesListView;
    public GridPane boardGrid;
    public TextField usernameTextField;


    @FXML
    public void initialize() {
        invitesListView.getItems().add("User#3                          [ACCEPT]  [DECLINE]");
        invitesListView.getItems().add("User#2                          [ACCEPT]  [DECLINE]");

        gamesListView.getItems().add("User#5 - In progress              [OPEN]  [PROFILE]");
        gamesListView.getItems().add("User#5 - Complete             [OPEN]  [PROFILE]");
        gamesListView.getItems().add("User#6 - Abandoned            [OPEN]  [PROFILE]");

        
    }

    public void profileButtonOnAction(ActionEvent event) {

    }

    public void inviteButtonOnAction(ActionEvent event) {

    }
}
