package Lobby;

import Client_Server.Chat.Message;
import Client_Server.Client.Client;
import Game.*;
import Localisation.Localisator;
import Login.LoginController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Benjamin Probst on 06.10.2017.
 */
public class LobbyController {

    private LobbyView lobbyView;
    private HandCardController handCardController;
    private GameView gameView;
    private Localisator localisator;
    private FieldCardController fieldCardController;
    private GameModel gameModel;
    private Client client;
    private GameController gameController;

    public LobbyController(LobbyView lobbyView, Localisator localisator, Client client) {
        this.lobbyView = lobbyView;
        this.localisator = localisator;
        this.client = client;
        if (client.getMusicActivated()) {
            Platform.runLater(() -> lobbyView.musicButton.getStyleClass().clear());
            Platform.runLater(() -> lobbyView.musicButton.getStyleClass().add("musicButtonOn"));
        } else {
            Platform.runLater(() -> lobbyView.musicButton.getStyleClass().clear());
            Platform.runLater(() -> lobbyView.musicButton.getStyleClass().add("musicButtonOff"));
        }

        /**
         * Eventhandler für das Drücken des Startbuttons
         */
        lobbyView.startButton.setOnAction(event -> {

            //TODO: Add player count
            if (!client.isGameStarted()) {
                if (client.isServer()) {
                    if (lobbyView.roundLimit.isSelected()) {
                        client.sendObject(new Message(7, "rounds", lobbyView.round.getValue()));
                    }
                    client.sendObject(new Message(4, client.getClientName(), "start"));
                } else {
                    if (lobbyView.startButton.getText().equals(localisator.getResourceBundle().getString("start"))) {
                        client.sendObject(new Message(1, client.getClientName(), localisator.getResourceBundle().getString("ready"), Client.getColor()));
                        lobbyView.startButton.setText(localisator.getResourceBundle().getString("ready"));
                    } else {
                        client.sendObject(new Message(1, client.getClientName(), localisator.getResourceBundle().getString("unready"), Client.getColor()));
                        lobbyView.startButton.setText(localisator.getResourceBundle().getString("start"));
                    }
                }
            }
        });

        /**
         * Eventhandler für den Zuschauer Modus. Startet ein Spielfeld um zuzuschauen
         */
        lobbyView.spectatorButton.setOnAction(event -> {
            if (client.isGameStarted()){
                startGame();
            }else{
                Alert quoteText = new Alert(Alert.AlertType.INFORMATION);
                quoteText.setTitle(localisator.getResourceBundle().getString("note"));
                quoteText.setHeaderText((localisator.getResourceBundle().getString("visitor")));
                quoteText.setContentText(localisator.getResourceBundle().getString("visitorInfo"));
                quoteText.showAndWait();
            }
        });

        /**
         * Eventhandler für den Chat Senden Button
         */
        lobbyView.chatWindow.getSendButton().setOnAction(event -> {
            if (lobbyView.chatWindow.getTxtMessage().getText() == null || lobbyView.chatWindow.getTxtMessage().getText().trim().isEmpty()) {
            }else {
                String text = lobbyView.chatWindow.getMessage();
                Message message = new Message(1, client.getClientName(), text, Client.getColor());
                client.sendObject(message);
                lobbyView.chatWindow.clearMessageField();
            }
        });

        /**
         * Eventhandler für das betätigen der Enter Taste
         */
        lobbyView.chatWindow.getTxtMessage().setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)){
                lobbyView.chatWindow.getSendButton().fire();
            }
        });


        lobbyView.primaryStage.setOnCloseRequest(event -> {
            client.stopClient();
            Platform.exit();
            System.exit(0);
        });

        lobbyView.musicButton.setOnAction(event -> {
            if (client.getMusicActivated()){
                client.stopMusic();
                client.setMusicActivated(false);
                Platform.runLater(() -> lobbyView.musicButton.getStyleClass().clear());
                Platform.runLater(() -> lobbyView.musicButton.getStyleClass().add("musicButtonOff"));
            } else {
                client.startBackground();
                client.setMusicActivated(true);
                Platform.runLater(() -> lobbyView.musicButton.getStyleClass().clear());
                Platform.runLater(() -> lobbyView.musicButton.getStyleClass().add("musicButtonOn"));
            }
        });

        lobbyView.roundLimit.setOnAction(event -> {
            if (lobbyView.roundLimit.isSelected()){
                lobbyView.round.setVisible(true);
            } else {
                lobbyView.round.setVisible(false);
            }
        });

    }

    public LobbyView getLobbyView() {
        return lobbyView;
    }

    public void startGame(){
        Stage gameStage = new Stage();
        gameView = new GameView(gameStage, localisator, lobbyView.getChatWindow(), client.getResolution(), localisator.getLanguage());
        gameModel = new GameModel(client);
        gameController = new GameController(gameView, localisator, gameModel, client);
        client.setGameController(gameController);

        gameView.start();
        lobbyView.stop();
    }

    public void showAddress(){
        if (client.isServer()){
            try {
                lobbyView.addressLabel.setText(localisator.getResourceBundle().getString("ip") + " " + InetAddress.getLocalHost().getHostAddress());
                lobbyView.addressLabel.setVisible(true);
                lobbyView.roundLimit.setVisible(true);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

}
