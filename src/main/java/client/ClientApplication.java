package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application{


    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login-prototype.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Hnefatafl");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
