package client;


import common.RegisterRequestEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RegisterController implements RegisterListener {


    @FXML
    public TextField portTextField;
    @FXML
    public TextField hostTextField;

    @FXML
    public TextField registerEmailField;
    @FXML
    public TextField registerUsernameField;
    @FXML
    public PasswordField registerPasswordField;

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private Client client;

    @FXML
    public void initialize() {
        Platform.runLater(() -> registerUsernameField.getScene().getWindow().setOnCloseRequest(event -> {
            if (client != null) {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    logger.error("Error closing client connection", e);
                }
            }
        }));
    }

    public void registerUserAction(ActionEvent event){
        try {
            client = new Client(hostTextField.getText(), Integer.parseInt(portTextField.getText()));
            client.addRegisterListener(this);

            String registerEmail = registerEmailField.getText();
            String registerUsername = registerUsernameField.getText();
            String registerPassword = registerPasswordField.getText();

            client.sendToServer(new RegisterRequestEvent(registerEmail, registerUsername, registerPassword));
        } catch (IOException e) {
            logger.error("Error sending register request to server", e);
        }
    }

    public void cancelRegisterOnAction(ActionEvent event) {
        if (client != null) {
            try {
                client.disconnect();
            } catch (IOException e) {
                logger.error("Error closing client connection", e);
            }
        }

        closeRegisterWindow();
    }

    private void closeRegisterWindow() {
        if (client != null) client.removeRegisterListener(this);
        Platform.runLater(() -> registerUsernameField.getScene().getWindow().hide());
    }

    @Override
    public void registerSucceeded(String email, String name, String password){
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Register Succeeded for User: " + name);
            a.setTitle("Success");
            a.showAndWait();
        });

        closeRegisterWindow();
    }

    @Override
    public void registerFailed(String email, String name, String error){
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Register failed");
            a.setTitle("Error");
            a.setContentText(error);
            a.showAndWait();
        });

        if (client != null) {
            try {
                client.disconnect();
            } catch (IOException e) {
                logger.error("Error while disconnecting client after failed register attempt", e);
            }
        }
    }


}
