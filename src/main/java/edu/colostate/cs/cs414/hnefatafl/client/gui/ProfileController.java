package edu.colostate.cs.cs414.hnefatafl.client.gui;


import edu.colostate.cs.cs414.hnefatafl.common.Profile;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.text.DecimalFormat;

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

        ((Stage) gameListView.getScene().getWindow()).setTitle("Profile of: " + profile.getId().getName());

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        statsLabel.setText(profile.getHistory().size() + " games played with " + df.format(profile.getWinrate()*100) + "% win rate");
    }

}
