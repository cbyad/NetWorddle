package com.cb.client.graphic;

import com.cb.client.ProtocolMessage;
import com.cb.client.Constants;
import com.cb.server.PuzzleGenerator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

/**
 * Controller du pauvre  ^(-__-)^
 */
public class Controller implements ProtocolMessage {
    public static final int BUTTON_SIZE = 15;
    public static final int LABEL_SIZE = 20;

     Stage primaryStage;

    //Declarer tous les composents graphiques
    TextField usernameField;
    PasswordField passwordField;
    TextField portField;
    TextField propositionField;
    TextField hostNameField;

    Button connectButton;
    Button disconnectButton;

    Button proposeButton;
    Button gridBUtton[][];
    Button portButton;

    Scene homeScene;
    Scene gameScene;
    VBox homeBox;
    VBox userDetailsBox;
    BorderPane gamePane;

    TextField msgSendField;
    TextField userSendField;
    Button sendButton;
     HBox connectDisconnetBox;
     GridPane gridPane;

    ListView<String> rcvdMsgsListView;
    Label timerLabel;
     Integer timeSeconds ;
     Timeline timeline;

     FxSocketClient socket;
    private final static Logger LOGGER
            = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private ObservableList<String> rcvdMsgsData;

    private boolean connected;

    @Override
    public void toConnect(String username, String password) {
        if (!username.isEmpty() && !password.isEmpty()) {
            String sendMessage = "connect" + "," + username + "," + password;
            socket.sendMessage(sendMessage);
            return;
        }
    }

    @Override
    public void toDisconnect() {
        socket.sendMessage("disconnect");
    }

    @Override
    public void toPropose(String word) {
        if (!word.isEmpty()) {
            String sendMessage = "proposition" + "," + word;
            socket.sendMessage(sendMessage);
            return;
        }
    }

    @Override
    public void sendPrivateMessage(String username, String message) {
        if (!username.isEmpty() && !message.isEmpty()) {
            String sendMessage = "send" + "," + username + "," + message;
            socket.sendMessage(sendMessage);
            return;
        }
    }

    @Override
    public void getSelfScore() {

    }

    @Override
    public void getOtherScore(String username) {

    }

    public enum ConnectionDisplayState {
        DISCONNECTED, CONNECTED
    }


    /*
     * Synchronized method responsible for notifying waitForDisconnect()
     * method that it's OK to stop waiting.
     */
    private synchronized void notifyDisconnected() {
        connected = false;
        notifyAll();
    }

    /*
     * Synchronized method to set isConnected boolean
     */
    private synchronized void setIsConnected(boolean connected) {
        this.connected = connected;
    }

    /*
     * Synchronized method to check for value of connected boolean
     */
    private synchronized boolean isConnected() {
        return (connected);
    }

    private void connect() {
        socket = new FxSocketClient(new FxSocketListener(),
                hostNameField.getText(),
                Integer.valueOf(portField.getText()),
                Constants.instance().DEBUG_NONE);
        socket.connect();
        isConnected();
    }


//==================================================================================================================


    public Controller(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.propositionField = new TextField();
        this.proposeButton = new Button("submit");
        this.msgSendField = new TextField();
        this.userSendField = new TextField();
        this.sendButton = new Button("send");
        this.disconnectButton = new Button("disconnect");
        this.connectButton = new Button("connect");
        this.passwordField = new PasswordField();
        this.usernameField = new TextField();
        this.portField = new TextField(String.valueOf(Constants.instance().DEFAULT_PORT));
        this.hostNameField = new TextField(Constants.instance().DEFAULT_HOST);
        this.portButton = new Button("Join server");
        this.rcvdMsgsListView = new ListView<>();
        this.gridPane = new GridPane();
        this.gamePane = new BorderPane();
        this.timerLabel = new Label();
        initGame();
    }


    public void initGame() {

        BorderPane homeRoot = new BorderPane();
        BorderPane gameRoot = new BorderPane();
        homeRoot.setStyle("-fx-background-color: #ff9999");
        gameRoot.setStyle("-fx-background-color: #ff9999");

        homeBox = buildHomeBox();
        homeRoot.setLeft(homeBox);
        homeScene = new Scene(homeRoot);

        // Scene comportant a gauche les details utilisateur et a droite le grille de jeux
        this.userDetailsBox = buildUserDetailsBox();
        gameRoot.setLeft(userDetailsBox);
        gameRoot.setRight(gamePane);
        gameRoot.setTop(null);
        gameRoot.setCenter(null);
        gameRoot.setBottom(null);
        this.gameScene = new Scene(gameRoot);

        bindAllListenerEvent();

        primaryStage.setTitle("NetWorddle");
        primaryStage.setScene(homeScene);
        //primaryStage.setResizable(false);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setOnCloseRequest(e -> {
                    Platform.exit();
                    socket.onClosedStatus(true);
                }
        );
        primaryStage.show();


        setIsConnected(false);
        displayState(Controller.ConnectionDisplayState.DISCONNECTED);

        rcvdMsgsData = FXCollections.observableArrayList();
        rcvdMsgsListView.setItems(rcvdMsgsData);
        rcvdMsgsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        rcvdMsgsListView.setOnMouseClicked((Event event) -> {
            String selectedItem
                    = rcvdMsgsListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && !selectedItem.equals("null")) {
                System.out.println(selectedItem);
            }
        });
        Runtime.getRuntime().addShutdownHook(new ShutDownThread());
    }

    private void bindAllListenerEvent() {

        portButton.setOnMouseClicked(e -> {
            if (!isConnected() && !portField.getText().isEmpty() && !hostNameField.getText().isEmpty() ) {
                connect();
                this.primaryStage.setScene(this.gameScene); // passer au mode jeu
            } else return;
        });

        connectButton.setOnMouseClicked(e -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            toConnect(username, password);
        });

        proposeButton.setOnMouseClicked(e -> {
            String word = propositionField.getText().toString();
            toPropose(word);
        });

        disconnectButton.setOnMouseClicked(e -> toDisconnect());

        sendButton.setOnMouseClicked(e -> {
            String username = userSendField.getText().toString();
            String message = msgSendField.getText().toString();
            sendPrivateMessage(username, message);
        });
    }

    /**
     * Acceuil
     *
     * @return
     */
    public VBox buildHomeBox() {
        //port
        Label port = new Label("Port");
        port.setFont(new Font(LABEL_SIZE));
        this.portButton.setFont(new Font(BUTTON_SIZE));

        // hostname
        Label host = new Label("Hostname");
        host.setFont(new Font(LABEL_SIZE));

        VBox vbox = new VBox(host, hostNameField, port, portField, portButton);
        return vbox;
    }

    /**
     * GAUCHE
     *
     * @return
     */
    public VBox buildUserDetailsBox() {

        //usermane
        Label username = new Label("Username ");
        username.setFont(new Font(LABEL_SIZE));

        //password
        Label password = new Label("Password ");

        // connectButton;
        this.connectDisconnetBox = new HBox(connectButton, disconnectButton);

        this.connectButton.setFont(new Font(BUTTON_SIZE));
        this.disconnectButton.setFont(new Font(BUTTON_SIZE));

        this.sendButton.setFont(new Font(BUTTON_SIZE));

        HBox h = new HBox(userSendField,msgSendField, sendButton);
        VBox vbox = new VBox(username, usernameField, password, passwordField, connectDisconnetBox, rcvdMsgsListView, h);
        return vbox;
    }


    public void buildGrid(int n, int m, char[][] grid,int time) {

        this.gridBUtton = new Button[n][m];
        for ( int i = 0; i < n; i++) {
            for ( int j = 0; j < m; j++) {
                gridBUtton[i][j] = new Button(grid[i][j]+"");
                gridBUtton[i][j].setPrefSize(70, 70);
                gridBUtton[i][j].setStyle("-fx-text-fill: #000000");
                gridBUtton[i][j].setFont(new Font(30));
                gridPane.add(gridBUtton[i][j], j, i);

            }
        }
        this.gamePane.setRight(gridPane);
        this.gamePane.setBottom(buildPropositionBox(time));

    }


    public HBox buildPropositionBox(int time) {
        Label proposition = new Label("Proposition");
        proposition.setFont(new Font(LABEL_SIZE));
        proposition.setFont(new Font(BUTTON_SIZE));

        timerLabel.setFont(new Font(LABEL_SIZE));
        timerLabel.setTextFill(Color.BLACK);
        timerLabel.setStyle("-fx-font-size: 4em;");

         timerLabel.setText(time+"");
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        (EventHandler) event1 -> {
                            timeSeconds--;
                            // mise a jour  du temps
                            timerLabel.setText(
                                    timeSeconds.toString());
                            if (timeSeconds <= 0) {
                                timeline.stop();
                            }
                        }));
        timeline.playFromStart();

        HBox hbox = new HBox(proposition, propositionField, proposeButton,timerLabel);
        return hbox;
    }


    //=============================================================================================================
    class ShutDownThread extends Thread {

        @Override
        public void run() {
            if (socket != null) {
                if (socket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                    LOGGER.info("ShutdownHook: Shutting down Server Socket");
                }
                socket.shutdown();
            }
        }
    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            if (line != null && !line.equals("")) {

                if (line.startsWith("start")) {
                    String[] parse = line.split(",");
                    int n = Integer.valueOf(parse[2]);
                    int m = Integer.valueOf(parse[3]);
                    char grid[][] = new char[n][m];
                    int pos = 4;
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < m; j++) {
                            grid[i][j] = parse[pos++].charAt(0);
                        }
                    }

                    timeSeconds=Integer.valueOf(parse[1]);
                    buildGrid(n, n, grid,timeSeconds);
                    rcvdMsgsData.add(parse[0]);
                    return;
                }
                rcvdMsgsData.add(line);
            }
        }


        @Override
        public void onClosedStatus(boolean isClosed) {
            if (isClosed) {
                notifyDisconnected();
                displayState(ConnectionDisplayState.DISCONNECTED);

            } else {
                setIsConnected(true);
                displayState(ConnectionDisplayState.CONNECTED);
            }
        }
    }

    private void displayState(Controller.ConnectionDisplayState state) {
        switch (state) {
            case DISCONNECTED:
               connectButton.setDisable(true);
                disconnectButton.setDisable(true);
                sendButton.setDisable(true);
                msgSendField.setDisable(true);
                propositionField.setDisable(true);
                proposeButton.setDisable(true);
                userSendField.setDisable(true);
                break;

            case CONNECTED:
                connectButton.setDisable(false);
                disconnectButton.setDisable(false);
                sendButton.setDisable(false);
                msgSendField.setDisable(false);
                propositionField.setDisable(false);
                proposeButton.setDisable(false);
                userSendField.setDisable(false);
                break;
        }
    }

}
