package client.gui;


import client.*;
import common.Invitation;
import common.event.invite.*;
import common.event.login.UnregisterRequestEvent;
import common.event.match.QuitMatchEvent;
import common.game.Match;
import common.event.match.PlayerMoveEvent;
import common.Profile;
import common.event.profile.RequestActiveInfoEvent;
import common.event.profile.RequestProfileEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ClientController implements MatchListener, MoveListener, ServerUtilListener, InviteListener {

    public ListView<String> invitesListView;
    public ListView<String> gamesListView;
    public TextField usernameTextField;
    public TabPane tabPane;

    private ContextMenu inviteListContextMenu;
    private ContextMenu gamesListContextMenu;

    private Client client;

    private final ArrayList<GameTab> gameTabs = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(ClientController.class);


    @FXML
    public void initialize() {
        MenuItem[] items = new MenuItem[2];
        items[0] = new MenuItem("Accept");
        items[0].setOnAction(event -> {
            try {
                client.sendToServer(new AcceptInviteEvent(Integer.parseInt(invitesListView.getSelectionModel().getSelectedItem())));
                invitesListView.getItems().remove(invitesListView.getSelectionModel().getSelectedIndex());
            } catch (IOException e) {
                logger.error("Error sending invite accept to server", e);
            }
        });
        items[1] = new MenuItem("Decline");
        items[1].setOnAction(event -> {
            try {
                client.sendToServer(new DeclineInviteEvent(Integer.parseInt(invitesListView.getSelectionModel().getSelectedItem())));
                invitesListView.getItems().remove(invitesListView.getSelectionModel().getSelectedIndex());
            } catch (IOException e) {
                logger.error("Error sending invite accept to server", e);
            }
        });
        inviteListContextMenu = new ContextMenu(items);
        invitesListView.setOnContextMenuRequested(event -> inviteListContextMenu.show(invitesListView, event.getScreenX(), event.getScreenY()));

        items = new MenuItem[1];
        items[0] = new MenuItem("Leave");
        items[0].setOnAction(event -> {
            final String work = gamesListView.getSelectionModel().getSelectedItem();
            final int attacker = Integer.parseInt(work.substring(0, work.indexOf('v')));
            final int defender = Integer.parseInt(work.substring(work.indexOf('v') + 1));

            for (GameTab tab : gameTabs) {
                if (tab.getMatch().getAttacker() == attacker && tab.getMatch().getDefender() == defender) {
                    try {
                        client.sendToServer(new QuitMatchEvent(tab.getMatch()));
                    } catch (IOException e) {
                        logger.error("Error sending quit match request", e);
                    }
                    break;
                }
            }
        });
        gamesListContextMenu = new ContextMenu(items);
        gamesListView.setOnContextMenuRequested(event -> gamesListContextMenu.show(gamesListView, event.getScreenX(), event.getScreenY()));
    }

    public void setClient(Client client) {
        if (this.client != null) {
            this.client.removeMatchListener(this);
            this.client.removeServerUtilListener(this);
            this.client.removeInviteListener(this);
        }

        //Close all current match tabs
        ArrayList<GameTab> tabs = new ArrayList<>();
        Collections.copy(tabs, gameTabs);
        for (GameTab tab : tabs) {
            endGame(tab.getMatch());
        }

        this.client = client;
        client.addMatchListener(this);
        client.addServerUtilListener(this);
        client.addInviteListener(this);

        //Request current matches on new client
        try {
            client.sendToServer(new RequestActiveInfoEvent());
        } catch (IOException e) {
            logger.error("Error sending current games request", e);
        }
    }

    public void profileButtonOnAction(ActionEvent event) {
        try {
            client.sendToServer(new RequestProfileEvent(usernameTextField.getText()));
        } catch (IOException e) {
            logger.error("Error sending history request for user: " + usernameTextField.getText(), e);
        }
    }

    public void inviteButtonOnAction(ActionEvent event) {
        try {
            client.sendToServer(new InviteUserEvent(usernameTextField.getText()));

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Invite sent");
            a.setContentText("Invite sent to: " + usernameTextField.getText());
            a.showAndWait();
        } catch (IOException e) {
            logger.error("Error sending invite to user: " + usernameTextField.getText(), e);
        }
    }

    private void openGame(Match match) {
        Platform.runLater(() -> {
            int enemyID = match.getAttacker();
            if (match.getAttacker() == client.getUserID()) enemyID = match.getDefender();

            GameTab tab = new GameTab("Against: " + enemyID, client.getUserID());
            gameTabs.add(tab);
            tabPane.getTabs().add(tab);

            tab.setMatch(match);
            tab.addMoveListener(this);

            gamesListView.getItems().add(match.getAttacker() + "v" + match.getDefender());
        });
    }

    private void endGame(Match match) {
        Platform.runLater(() -> {
            GameTab toRemove = null;

            for (GameTab tab : gameTabs) {
                if (match.equals(tab.getMatch())) {
                    tabPane.getTabs().remove(tab);
                    tab.removeMoveListener(this);
                    toRemove = tab;
                    break;
                }
            }

            if (toRemove != null) gameTabs.remove(toRemove);

            gamesListView.getItems().remove(match.getAttacker() + "v" + match.getDefender());
        });
    }

    @Override
    public void matchUpdated(Match match) {
        Platform.runLater(() -> {
            for (GameTab tab : gameTabs) {
                if (match.equals(tab.getMatch())) {
                    tab.setMatch(match);
                    break;
                }
            }
        });
    }

    @Override
    public void matchStarted(Match match) {
        openGame(match);
    }

    @Override
    public void matchFinished(Match match) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Result: " + match.getStatus());
            a.setTitle("Match finished");
            a.setHeaderText("Match finished between " + match.getAttacker() + " and " + match.getDefender());
            a.showAndWait();

            endGame(match);
        });
    }

    @Override
    public void playerRequestedMove(Match match, int fromRow, int fromCol, int toRow, int toCol) {
        int enemyID = match.getAttacker();
        if (enemyID == client.getUserID()) enemyID = match.getDefender();
        try {
            client.sendToServer(new PlayerMoveEvent(enemyID, fromRow, fromCol, toRow, toCol));
        } catch (IOException e) {
            logger.error("Error sending player move to server", e);
        }
    }

    @Override
    public void profileReceived(Profile profile) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile-prototype.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                ProfileController controller = loader.getController();
                controller.setProfile(profile);
            } catch (IOException e) {
                logger.error("Error opening profile fxml", e);
            }
        });
    }

    @Override
    public void noSuchUserError(String requestedUser) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("No such user: " + requestedUser);
            a.setContentText("A client request for a certain user found no user with the specified name");
            a.showAndWait();
        });
    }

    @Override
    public void inviteReceived(Invitation invite) {
        Platform.runLater(() -> {
            invitesListView.getItems().add(invite.getSenderID() + "");
        });
    }

    @Override
    public void inviteDeclined(Invitation invite) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Invitation declined");
            a.setContentText("Invitation to " + invite.getTargetID() + " was declined");
            a.showAndWait();
        });
    }

    @Override
    public void inviteAccepted(Invitation invite) {

    }

    public void unregisterMenuItemOnAction(ActionEvent event) {
        try {
            client.sendToServer(new UnregisterRequestEvent());
            logout();
        } catch (IOException e) {
            logger.error("Error sending unregister request event", e);
        }
    }

    private void logout() throws IOException {
        client.disconnect();

        tabPane.getScene().getWindow().hide();

        Parent root = FXMLLoader.load(getClass().getResource("/login-prototype.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Hnefatafl");
        stage.show();
    }

    public void logoutMenuItemOnAction(ActionEvent event) {
        try {
            logout();
        } catch (IOException e) {
            logger.error("Error loading login window fxml", e);
        }
    }

}
