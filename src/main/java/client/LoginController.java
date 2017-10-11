package client;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LoginController {

    @FXML
    public void initialize() {
        //Called after constructor but before any JFX calls
        //May not be useful for this
    }

    public void loginOnAction(ActionEvent event) {
        //TODO: Send login token
        //TODO: Process response
    }

    public void cancelOnAction(ActionEvent event) {
        //Exit program entirely
        Platform.exit();
    }

}
