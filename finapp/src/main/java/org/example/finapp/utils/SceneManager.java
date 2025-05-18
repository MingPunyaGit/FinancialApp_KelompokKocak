package org.example.finapp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.finapp.Main;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void showLoginView() throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("/org/example/finapp/login-view.fxml"));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Login");
    }

    public static void showMainView() throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("/org/example/finapp/main-view.fxml"));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Aplikasi Manajemen Keuangan");
    }
}