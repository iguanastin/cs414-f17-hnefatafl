package client;


import common.Event;
import common.LoginRequestEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;


    @FXML
    public void initialize() {
        //Called after constructor but before any JFX calls
        //May not be useful for this
    }

    public void loginOnAction(ActionEvent event) {
        //TODO: Send login token
        //TODO: Process response
        Client loginClient = null;
        try {
            loginClient = new Client("localhost",54321);
            loginClient.sendToServer(new LoginRequestEvent(loginUsername.getText(), loginPassword.getText()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelOnAction(ActionEvent event) {
        //Exit program entirely
        Platform.exit();
    }

}
