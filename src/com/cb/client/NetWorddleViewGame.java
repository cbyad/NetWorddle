package com.cb.client;

import com.cb.server.PuzzleGenerator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class NetWorddleViewGame extends Application {
    //Declarer tous les composents graphiques ici apres pour avoir main dessu
    TextField usernameField ;
    PasswordField passwordField ;

    Button connect ;
    Button disconnect;
    TextField propositionField ;
    Button propose ;
    Button gridTab[][];


    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        String path = "files/worddle/dicesets/american.diceset";
        String dict = "files/worddle/dictionaries/american-english.dict";
        PuzzleGenerator generator = new PuzzleGenerator(5, 5, path);
        char[][] grid = generator.getGrid();



        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ff9999");
        root.setLeft(userConnexionPane());
        root.setRight(buildGridPane(5,5,grid));

        // root.getChildren().add(buildGridPane(7,7,grid));
        Scene scene = new Scene(root);
        primaryStage.setTitle("NetWorddle");
        primaryStage.setScene(scene);
        primaryStage.setWidth(600);
        primaryStage.setResizable(false);
        primaryStage.setHeight(450);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> Platform.exit());


    }

    //Right
    public GridPane buildGridPane(int n, int m, char[][] grid) {

        GridPane gridPane = new GridPane();
        gridTab = new Button[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                gridTab[i][j] = new Button(grid[i][j] + "");
                gridTab[i][j].setPrefSize(70, 70);
                gridTab[i][j].setStyle("-fx-text-fill: #000000");
                gridTab[i][j].setFont(new Font(30));
                //gridTab[i][j].setDisable(true);
                gridPane.add(gridTab[i][j], i, j);

            }

        }
        //gridPane.setStyle("-fx-background-color: #3366ff");
        return gridPane;
    }

    //Left
    public VBox userConnexionPane() {
        Label username = new Label("Username ");
        username.setFont(new Font(15));
        this.usernameField = new TextField();

        Label password = new Label("Password ");
        password.setFont(new Font(15));
        this.passwordField = new PasswordField();

        this.connect = new Button("connect");
        connect.setFont(new Font(10));
        this.disconnect = new Button("disconnect");
        disconnect.setFont(new Font(10));

        //TextArea channel = new TextArea();
        this.propositionField = new TextField();
        this.propose = new Button("propose");
        propose.setFont(new Font(10));
        HBox hbox= new HBox(propositionField,propose);

        VBox vbox = new VBox(username, usernameField, password, passwordField, connect,disconnect,hbox);
        //vbox.setStyle("-fx-background-color: #ff9999");

        return vbox;
    }






}
