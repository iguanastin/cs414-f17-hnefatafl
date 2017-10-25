package client;


import Game.GameGUIRunner;
import common.Event;
import common.LoginRequestEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController implements LoginListener {

    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private Client client;


    @FXML
    public void initialize() {
        try {
            client = new Client("localhost", 54321);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addLoginListener(this);

        Platform.runLater(() -> {
            loginUsername.getScene().getWindow().setOnCloseRequest(event -> {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    logger.error("Error closing client connection", e);
                }
            });
        });
    }

    public void loginOnAction(ActionEvent event) {
        try {
            client.sendToServer(new LoginRequestEvent(loginUsername.getText(), loginPassword.getText()));
        } catch (IOException e) {
            logger.error("Error sending login request to server", e);
        }
    }

    public void cancelOnAction(ActionEvent event) {
        try {
            client.disconnect();
        } catch (IOException e) {
            logger.error("Error closing client connection", e);
        }

        closeWindow();
    }

    private void closeWindow() {
        client.removeLoginListener(this);
        Platform.runLater(() -> loginUsername.getScene().getWindow().hide());
    }

    @Override
    public void loginSucceeded(int id, String name) {
        Platform.runLater(() -> {
//            GameGUIRunner gui = new GameGUIRunner();
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-prototype.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
//                stage.setScene(new Scene(gui.createContent()));
                stage.setTitle("Hnefetafl");
                stage.show();
                stage.setOnCloseRequest(event -> {
                    try {
                        client.disconnect();
                    } catch (IOException e) {
                        logger.error("Error closing client connection", e);
                    }
                });
                ClientController controller = loader.getController();
                controller.setClient(client);
            } catch (IOException e) {
                logger.error("Error loading FXML doc", e);
            }

            closeWindow();
        });
    }

    @Override
    public void loginFailed(String name) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Login failed");
            a.setTitle("Error");
            a.setContentText("Unable to log in as user: " + name);
            a.showAndWait();
        });
    }
}
