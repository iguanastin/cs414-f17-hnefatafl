package client;


import Game.Match;
import common.InviteToMatchEvent;
import common.PlayerMoveEvent;
import common.RequestCurrentGamesEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ClientController implements MatchListener, MoveListener {

    public ListView<String> invitesListView;
    public ListView<String> gamesListView;
    public GridPane boardGrid;
    public TextField usernameTextField;
    public TabPane tabPane;

    private ContextMenu inviteListContextMenu;
    private ContextMenu gamesListContextMenu;

    private Client client;

    private final ArrayList<GameTab> gameTabs = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(ClientController.class);
    private String host;
    private int port;


    @FXML
    public void initialize() {
        MenuItem[] items = new MenuItem[2];
        items[0] = new MenuItem("Accept");
        items[1] = new MenuItem("Decline");
        inviteListContextMenu = new ContextMenu(items);
        invitesListView.setOnContextMenuRequested(event -> inviteListContextMenu.show(invitesListView, event.getScreenX(), event.getScreenY()));

        items = new MenuItem[1];
        items[0] = new MenuItem("Leave");
        gamesListContextMenu = new ContextMenu(items);
        gamesListView.setOnContextMenuRequested(event -> gamesListContextMenu.show(gamesListView, event.getScreenX(), event.getScreenY()));
    }

    public void setClient(Client client) {
        if (this.client != null) this.client.removeMatchListener(this);

        //Close all current match tabs
        ArrayList<GameTab> tabs = new ArrayList<>();
        Collections.copy(tabs, gameTabs);
        for (GameTab tab : tabs) {
            endGame(tab.getMatch());
        }

        this.client = client;
        client.addMatchListener(this);

        //Request current matches on new client
        try {
            client.sendToServer(new RequestCurrentGamesEvent());
        } catch (IOException e) {
            logger.error("Error sending current games request", e);
        }
    }

    public void profileButtonOnAction(ActionEvent event) {
        //TODO
    }

    public void inviteButtonOnAction(ActionEvent event) {
        try {
            client.sendToServer(new InviteToMatchEvent(usernameTextField.getText()));
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

}
