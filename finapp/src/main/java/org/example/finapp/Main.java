package org.example.finapp;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.finapp.services.AuthService;
import org.example.finapp.utils.SceneManager;

import java.io.IOException;
import java.util.prefs.Preferences;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        SceneManager.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Aplikasi Keuangan FinApp");

        if (attemptAutoLogin()) {
            SceneManager.showMainView();
        } else {
            SceneManager.showLoginView();
        }

        primaryStage.show();
    }

    private boolean attemptAutoLogin() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String lastUser = prefs.get("lastLoggedInUser", null);

        if (lastUser != null) {
            AuthService authService = new AuthService();
            return authService.loginFromSession(lastUser);
        }

        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}