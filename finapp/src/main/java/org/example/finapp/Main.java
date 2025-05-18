package org.example.finapp;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.finapp.utils.SceneManager;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.showLoginView();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}