package Login;

import Client_Server.Client.Client;
import Client_Server.Chat.Message;
import Client_Server.Server.StartServer;
import Dialogs.DialogController;
import Dialogs.DialogView;
import Lobby.LobbyController;
import Lobby.LobbyView;
import Localisation.Localisator;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import static javafx.scene.media.AudioClip.INDEFINITE;

/**
 * Created by Benjamin Probst on 06.10.2017.
 */

public class LoginController {

    private LoginModel loginModel;
    private LoginView loginView;
    private LobbyView lobbyView;
    private Stage primaryStage;
    private LobbyController lobbyController;
    protected Localisator localisator;
    private String clientName;
    private Client client;
    private String resolution = "720p";
    private boolean musicActivated = true;
    private static AudioClip audioClip;

    public LoginController(LoginModel loginModel, LoginView loginView, Stage primaryStage, Localisator localisator) {
        this.loginModel = loginModel;
        this.loginView = loginView;
        this.primaryStage = primaryStage;
        this.localisator = localisator;

        /**
         * Eventhandler für den Join Button
         */
        loginView.joinButton.setOnAction(event -> {

            if (loginView.userNameField.getText() == null || loginView.userNameField.getText().trim().isEmpty()){
                loginView.userNameField.setPromptText(localisator.getResourceBundle().getString("UsernameNeeded"));
                loginView.userNameField.getStyleClass().clear();
                loginView.userNameField.getStyleClass().add("userNameNeeded");
            } else {
                DialogView dialogView = new DialogView(localisator.getResourceBundle().getString("addressText"), "localhost", "IP Address", localisator);
                DialogController dialogController = new DialogController(dialogView, localisator, this);
                dialogView.start();
            }
        });

        /**
         * Eventhandler für den Host Button
         */
        loginView.hostButton.setOnAction(event -> {

            if (loginView.userNameField.getText() == null || loginView.userNameField.getText().trim().isEmpty()) {
                loginView.userNameField.setPromptText(localisator.getResourceBundle().getString("UsernameNeeded"));
                loginView.userNameField.getStyleClass().clear();
                loginView.userNameField.getStyleClass().add("userNameNeeded");
            } else {
                clientName = loginView.userNameField.getText();
                StartServer startServer = new StartServer();
                startServer.start();
                client = new Client("localhost", clientName, resolution, audioClip, musicActivated);
                client.start();
                client.sendObject(new Message(0, clientName, "login"));
                lobbyView = new LobbyView(primaryStage, localisator);
                lobbyController = new LobbyController(lobbyView, localisator, client);
                client.setLobbyController(lobbyController);
                client.actualizePlayers();
                client.setServer();
                lobbyController.showAddress();
            }
        });

        /**
         * Created by camillo.schweizer
         *
         * Eventhändler für die languageBox - startet die Updatemethde nach auswahl einer Sprache. Zudem wird languageUpdate
         * aufgerufen, um die View zu aktualisieren
         */
        loginView.languageBox.setOnAction(event -> {
            String language = loginView.languageBox.getValue();
            languageChecker(language);
            languageUpdate();
        });

        /**
         * Created by camillo.schweizer
         *
         * Eventhändler für die sizeBox - startet die Updatemethde
         */
        loginView.sizeBox.setOnAction(event -> {
            this.resolution = loginView.sizeBox.getValue();
        });

        /**
         * Eventhandler für den Musik Button
         */
        loginView.musicButton.setOnAction(event -> {
            if (musicActivated){
                stopMusic();
                musicActivated = false;
                Platform.runLater(() -> loginView.musicButton.getStyleClass().clear());
                Platform.runLater(() -> loginView.musicButton.getStyleClass().add("musicButtonOff"));
            } else {
                startBackground();
                musicActivated = true;
                Platform.runLater(() -> loginView.musicButton.getStyleClass().clear());
                Platform.runLater(() -> loginView.musicButton.getStyleClass().add("musicButtonOn"));
            }
        });

        startBackground();

        loginView.primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Created by camillo.schweizer
     *
     * Wird vom Eventhändler der languageBox aufgerufen um die Sprache beim Localisator zu ändern
     */

    public void languageChecker(String language) {

        switch (language) {
            case "Schwiizerdütsch":
                localisator.switchCH();
                break;
            case "Deutsch":
                localisator.switchGER();
                break;
            case "English":
                localisator.switchENG();
                break;
            default:
                break;
        }
    }

    /**
     * Created by camillo.schweizer
     *
     * Wird vom Eventhändler der languageBox aufgerufen, um die View zu aktualisieren
     */
    public void languageUpdate(){

        loginView.userNameLabel.setText(localisator.getResourceBundle().getString("username"));
        loginView.hostButton.setText(localisator.getResourceBundle().getString("hosting"));
        loginView.joinButton.setText(localisator.getResourceBundle().getString("join"));
        loginView.userNameField.setPromptText(localisator.getResourceBundle().getString("username"));
        loginView.userNameField.getStyleClass().clear();
        loginView.userNameField.getStyleClass().add("text-field");
        loginView.languageBox.setPromptText(localisator.getResourceBundle().getString("language"));
        loginView.connectingLabel.setText(localisator.getResourceBundle().getString("connecting"));
        loginView.dialog.setTitle(localisator.getResourceBundle().getString("addressTitle"));
        loginView.dialog.setHeaderText(localisator.getResourceBundle().getString("addressHeader"));
        loginView.dialog.setContentText(localisator.getResourceBundle().getString("addressText"));
        loginView.conError.setTitle(localisator.getResourceBundle().getString("conErrorTitle"));
        loginView.conError.setHeaderText(localisator.getResourceBundle().getString("conErrorHeader"));
        loginView.conError.setContentText(localisator.getResourceBundle().getString("conErrorText"));
    }

    /**
     * Methode um eine Verbindung mit dem Server herzustellen
     * @param address benötigt eine IP Adresse als String
     * @return Status des Verbindungsaufbaus
     */
    public String connect(String address){
        clientName = loginView.userNameField.getText();

        if (client == null) {
            client = new Client(address, clientName, resolution, audioClip, musicActivated);
            if (client.isConnected()) {
                client.start();
            }
        }
        loginView.connectingLabel.setVisible(false);

        if (!client.isFailure()) {
            Message user = new Message(0, clientName, "login");
            client.sendObject(user);
            while (!client.isChecked()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Waiting until server response for username validation
            }
            if (client.isValid()) {
                lobbyView = new LobbyView(primaryStage, localisator);
                lobbyController = new LobbyController(lobbyView, localisator, client);
                client.setLobbyController(lobbyController);
                client.actualizePlayers();
                return "successful";
            } else {
                loginView.userNameField.setPromptText(localisator.getResourceBundle().getString("validUsername"));
                loginView.userNameField.getStyleClass().clear();
                loginView.userNameField.getStyleClass().add("text-field");
                client.resetChecked();
                return "username used";
            }

        } else {
            return "Error connecting";
        }
    }

    /**
     * Methode um ein Audio File abzuspielen.
     * Kopiert von: https://stackoverflow.com/questions/31784698/javafx-background-thread-task-should-play-music-in-a-loop-as-background-thread
     */

    public static void startBackground(){

        final Task task = new Task() {

            @Override
            protected Object call() throws Exception {
                int s = INDEFINITE;
                audioClip = new AudioClip(getClass().getResource("/Sounds/background.wav").toExternalForm());
                audioClip.setVolume(0.09);
                audioClip.setCycleCount(s);
                audioClip.play();
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Methode um die Musik zu stoppen
     */
    public void stopMusic(){
        this.musicActivated = false;
        audioClip.stop();
    }

    public LoginModel getLoginModel() {
        return loginModel;
    }

    public LoginView getLoginView() {
        return loginView;
    }

    public Client getClient() {
        return client;
    }
}



