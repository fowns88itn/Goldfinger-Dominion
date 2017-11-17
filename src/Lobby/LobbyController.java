package Lobby;

import Client_Server.Chat.Message;
import Client_Server.Client.Client;
import Game.*;
import Localisation.Localisator;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Created by Benjamin Probst on 06.10.2017.
 */
public class LobbyController {

    private LobbyModel lobbyModel;
    private LobbyView lobbyView;
    private HandCardController handCardController;
    private GameView gameView;
    private Localisator localisator;
    private FieldCardController fieldCardController;
    private GameModel gameModel;
    private Client client;
    private GameController gameController;

    public LobbyController(LobbyModel lobbyModel, LobbyView lobbyView, Localisator localisator, Client client) {
        this.lobbyModel = lobbyModel;
        this.lobbyView = lobbyView;
        this.localisator = localisator;
        this.client = client;

        lobbyView.startButton.setOnAction(event -> {

            //TODO: Add player count
            if (!client.isGameStarted()) {
                if (client.isServer()) {
                    client.sendObject(new Message(4, client.getClientName(), "started"));
                } else {
                    if (lobbyView.startButton.getText().equals(localisator.getResourceBundle().getString("start"))) {
                        client.sendObject(new Message(1, client.getClientName(), "ready", client.getColor()));
                        lobbyView.startButton.setText(localisator.getResourceBundle().getString("ready"));
                    } else {
                        client.sendObject(new Message(1, client.getClientName(), "unready", client.getColor()));
                        lobbyView.startButton.setText(localisator.getResourceBundle().getString("start"));
                    }
                }
            }
        });

        lobbyView.spectatorButton.setOnAction(event -> {
            if (client.isGameStarted()){
                startGame();
            }
        });

        lobbyView.chatWindow.getSendButton().setOnAction(event -> {
            if (lobbyView.chatWindow.getTxtMessage().getText() == null || lobbyView.chatWindow.getTxtMessage().getText().trim().isEmpty()) {
            }else {
                String text = lobbyView.chatWindow.getMessage();
                Message message = new Message(1, client.getClientName(), text, Client.getColor());
                client.sendObject(message);
                lobbyView.chatWindow.clearMessageField();
            }
        });

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


    }

    public LobbyView getLobbyView() {
        return lobbyView;
    }

    public LobbyModel getLobbyModel() {
        return lobbyModel;
    }

    public void startGame(){
        Stage gameStage = new Stage();
        gameView = new GameView(gameStage, localisator, lobbyView.getChatWindow());
        gameModel = new GameModel(client);
        gameController = new GameController(gameView, localisator, gameModel, client);
        client.setGameController(gameController);

        gameView.start();
        lobbyView.stop();
    }

}
