package client;


import common.LoginRequestEvent;
import common.RegisterRequestEvent;
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

    public TextField registerEmailField;
    public TextField registerUsernameField;
    public PasswordField registerPasswordField;

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
        });
    }

    public void loginOnAction(ActionEvent event) {
        try {
            client = new Client(hostTextField.getText(), Integer.parseInt(portTextField.getText()));
        } catch (IOException e) {
            logger.error("Error connecting to server " + hostTextField.getText() + ":" + portTextField.getText(), e);
            loginFailed(usernameTextField.getText());
        }
        client.addLoginListener(this);

        try {
            client.sendToServer(new LoginRequestEvent(usernameTextField.getText(), passwordTextField.getText()));
        } catch (IOException e) {
            logger.error("Error sending login request to server", e);
        }
    }

    public void registerUserAction(ActionEvent event){
        try {
            client = new Client(hostTextField.getText(), Integer.parseInt(portTextField.getText()));
        } catch (IOException e) {
            logger.error("Error connecting to server " + hostTextField.getText() + ":" + portTextField.getText(), e);
            loginFailed(usernameTextField.getText());
        }

        try {
            String registerEmail = registerEmailField.getText();
            String registerUsername = registerUsernameField.getText();
            String registerPassword = registerPasswordField.getText();

            client.sendToServer(new RegisterRequestEvent(registerEmail, registerUsername, registerPassword));
        } catch (IOException e) {
            logger.error("Error sending register request to server", e);
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

            closeRegisterWindow();
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

    private void closeWindow() {
        if (client != null) client.removeLoginListener(this);
        Platform.runLater(() -> usernameTextField.getScene().getWindow().hide());
    }

    private void closeRegisterWindow() {
        if (client != null) client.removeLoginListener(this);
        Platform.runLater(() -> registerUsernameField.getScene().getWindow().hide());
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

    @Override
    public void registerSucceeded(String email, String name, String password){
        System.out.println("YAY");
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
