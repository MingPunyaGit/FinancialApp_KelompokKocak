// File: src/main/java/org/example/finapp/Main.java
package org.example.finapp;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.finapp.services.AuthService;
import org.example.finapp.services.DatabaseService;
import org.example.finapp.utils.SceneManager;

import java.io.IOException;
import java.util.prefs.Preferences;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        DatabaseService.initializeDatabase();

        SceneManager.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Aplikasi Keuangan FinApp");

        // Atur ukuran minimum window (pengguna tidak bisa resize lebih kecil dari ini)
        primaryStage.setMinWidth(1200); // Anda bisa sesuaikan jika 1280 terlalu lebar
        primaryStage.setMinHeight(700); // Anda bisa sesuaikan jika 720 terlalu tinggi

        // Atur ukuran awal window saat pertama kali muncul (INI YANG BARU)
        primaryStage.setWidth(1280);  // Sesuaikan dengan lebar yang Anda inginkan
        primaryStage.setHeight(720); // Sesuaikan dengan tinggi yang Anda inginkan

        if (attemptAutoLogin()) {
            SceneManager.showMainView(); // Scene akan dimuat di sini
        } else {
            SceneManager.showLoginView(); // Scene akan dimuat di sini
        }

        // Pindahkan centerOnScreen setelah scene dimuat jika ada, atau sebelum show
        // SceneManager kita sudah menghandle centerOnScreen, jadi ini bisa jadi redundant
        // primaryStage.centerOnScreen();

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