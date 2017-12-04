package edu.colostate.cs.cs414.hnefatafl.client.gui;


import edu.colostate.cs.cs414.hnefatafl.client.*;
import edu.colostate.cs.cs414.hnefatafl.common.Invitation;
import edu.colostate.cs.cs414.hnefatafl.common.UserID;
import edu.colostate.cs.cs414.hnefatafl.common.event.invite.*;
import edu.colostate.cs.cs414.hnefatafl.common.event.login.UnregisterRequestEvent;
import edu.colostate.cs.cs414.hnefatafl.common.event.match.QuitMatchEvent;
import edu.colostate.cs.cs414.hnefatafl.common.game.Match;
import edu.colostate.cs.cs414.hnefatafl.common.event.match.PlayerMoveEvent;
import edu.colostate.cs.cs414.hnefatafl.common.Profile;
import edu.colostate.cs.cs414.hnefatafl.common.event.profile.RequestActiveInfoEvent;
import edu.colostate.cs.cs414.hnefatafl.common.event.profile.RequestProfileEvent;
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

    public ListView<UserID> invitesListView;
    public ListView<UserID> gamesListView;
    public TextField usernameTextField;
    public TabPane tabPane;

    private Client client;

    private final ArrayList<GameTab> gameTabs = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(ClientController.class);


    @FXML
    public void initialize() {
        invitesListView.setCellFactory((ListView<UserID> param) -> new InviteListCell(new InviteListCellListener() {
            @Override
            public void acceptClicked(UserID user) {
                try {
                    client.sendToServer(new AcceptInviteEvent(user));
                    invitesListView.getItems().remove(user);
                } catch (IOException e) {
                    logger.error("Error sending invite accept to server", e);
                }
            }

            @Override
            public void declineClicked(UserID user) {
                try {
                    client.sendToServer(new DeclineInviteEvent(user));
                    invitesListView.getItems().remove(user);
                } catch (IOException e) {
                    logger.error("Error sending invite accept to server", e);
                }
            }
        }));

        gamesListView.setCellFactory((ListView<UserID> param) -> new GameListCell(id -> {
            try {
                Match match = getMatch(client.getUserID(), id);
                if (match == null) match = getMatch(id, client.getUserID());

                client.sendToServer(new QuitMatchEvent(match));
            } catch (IOException e) {
                logger.error("Error sending quit match request", e);
            }
        }));
    }

    private Match getMatch(UserID attacker, UserID defender) {
        for (GameTab tab : gameTabs) {
            if (tab.getMatch().getAttacker().equals(attacker) && tab.getMatch().getDefender().equals(defender)) {
                return tab.getMatch();
            }
        }
        return null;
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
            UserID enemy = match.getAttacker();
            if (match.getAttacker().equals(client.getUserID())) enemy = match.getDefender();

            GameTab tab = new GameTab("Against: " + enemy.getName(), client.getUserID());
            gameTabs.add(tab);
            tabPane.getTabs().add(tab);

            tab.setMatch(match);
            tab.addMoveListener(this);

            gamesListView.getItems().add(enemy);
        });
    }

    private void endGame(Match match) {
        Platform.runLater(() -> {
            GameTab toRemove = null;

            for (GameTab tab : gameTabs) {
                if (match.equals(tab.getMatch())) {
                    toRemove = tab;
                    break;
                }
            }
            if (toRemove != null) {
                gameTabs.remove(toRemove);
                tabPane.getTabs().remove(toRemove);
                toRemove.removeMoveListener(this);
            }

            UserID enemy = match.getAttacker();
            if (client.getUserID().equals(enemy)) enemy = match.getDefender();
            gamesListView.getItems().remove(enemy);
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
        UserID enemy = match.getAttacker();
        if (enemy.equals(client.getUserID())) enemy = match.getDefender();
        try {
            client.sendToServer(new PlayerMoveEvent(enemy, fromRow, fromCol, toRow, toCol));
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
            invitesListView.getItems().add(invite.getSender());
        });
    }

    @Override
    public void inviteDeclined(Invitation invite) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Invitation declined");
            a.setContentText("Invitation to " + invite.getTarget().getName() + " was declined");
            a.showAndWait();
        });
    }

    @Override
    public void inviteAccepted(Invitation invite) {

    }

    @Override
    public void inviteAlreadyExists(UserID id) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Invitation already exists");
            a.setContentText("There is currently a pending invite between you and " + id.getName());
            a.showAndWait();
        });
    }

    @Override
    public void alreadyInMatch(UserID id) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Already in match");
            a.setContentText("You are already in a match with " + id.getName());
            a.showAndWait();
        });
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
