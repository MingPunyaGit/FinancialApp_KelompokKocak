package org.example.finapp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.finapp.Main;

import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    private static void loadScene(String fxmlPath) throws IOException {
        URL fxmlLocation = Main.class.getResource(fxmlPath);
        if (fxmlLocation == null) {
            System.err.println("Tidak dapat menemukan file FXML di: " + fxmlPath);
            throw new IOException("Resource not found: " + fxmlPath);
        }
        Parent root = FXMLLoader.load(fxmlLocation);
        primaryStage.setScene(new Scene(root));
        primaryStage.centerOnScreen();
    }

    public static void showLoginView() throws IOException {
        loadScene("/org/example/finapp/login-view.fxml");
    }

    public static void showRegisterView() throws IOException {
        loadScene("/org/example/finapp/register-view.fxml");
    }

    public static void showMainView() throws IOException {
        loadScene("/org/example/finapp/main-view.fxml");
    }
}