package client.gui;


import common.Profile;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ProfileController {

    public ListView<String> gameListView;
    public Label statsLabel;


    /**
     * Displays the profile
     *
     * @param profile
     */
    public void setProfile(Profile profile) {
        gameListView.getItems().clear();
        profile.getHistory().forEach(m -> gameListView.getItems().add(m.toString()));

        ((Stage) gameListView.getScene().getWindow()).setTitle(profile.getName() + " - " + profile.getId());

        statsLabel.setText(profile.getHistory().size() + " games played with " + (profile.getWinrate()*100) + "% winrate");
    }

}
