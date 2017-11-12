package client;

import common.RegisterRequestEvent;
import common.event.login.LoginRequestEvent;
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

    public TextField portTextField;
    public TextField hostTextField;
    public TextField usernameTextField;
    public PasswordField passwordTextField;

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private Client client;


    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            usernameTextField.getScene().getWindow().setOnCloseRequest(event -> {
                if (client != null) {
                    try {
                        client.disconnect();
                    } catch (IOException e) {
                        logger.error("Error closing client connection", e);
                    }
                }
            });

            usernameTextField.requestFocus();
        });
    }

    public void loginOnAction(ActionEvent event) {
        try {
            client = new Client(hostTextField.getText(), Integer.parseInt(portTextField.getText()));
            client.addLoginListener(this);

            try {
                client.sendToServer(new LoginRequestEvent(usernameTextField.getText(), passwordTextField.getText()));
            } catch (IOException e) {
                logger.error("Error sending login request to server", e);
            }
        } catch (IOException e) {
            logger.error("Error connecting to server " + hostTextField.getText() + ":" + portTextField.getText(), e);
            loginFailed(usernameTextField.getText());
        }
    }

    public void openRegisterAction() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/register-prototype.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.setTitle("Register");
                stage.show();

            } catch (IOException e) {
                logger.error("Error loading FXML doc", e);
            }
        });
    }

    public void cancelOnAction(ActionEvent event) {
        if (client != null) {
            try {
                client.disconnect();
            } catch (IOException e) {
                logger.error("Error closing client connection", e);
            }
        }

        closeWindow();
    }

   private void closeWindow() {
        if (client != null) client.removeLoginListener(this);
        Platform.runLater(() -> usernameTextField.getScene().getWindow().hide());
    }

    @Override
    public void loginSucceeded(int id, String name) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client-prototype.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
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

        if (client != null) {
            try {
                client.disconnect();
            } catch (IOException e) {
                logger.error("Error while disconnecting client after failed login attempt", e);
            }
        }
    }
}
