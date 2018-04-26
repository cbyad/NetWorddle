package com.cb.client.graphic;
import javafx.application.Application;


import javafx.stage.Stage;


public class NetWorddleIHM extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
         new Controller(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}