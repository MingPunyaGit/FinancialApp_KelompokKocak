package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.finapp.Main;
import org.example.finapp.services.AuthService;
import org.example.finapp.utils.AlertManager;
import org.example.finapp.utils.SceneManager;

import java.io.IOException;
import java.util.prefs.Preferences;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authService.login(username, password)) {
            if (rememberMeCheckBox.isSelected()) {
                saveLastUser(username);
            } else {
                clearLastUser();
            }
            SceneManager.showMainView();
        } else {
            AlertManager.showError("Login Gagal", "Username atau password salah.");
        }
    }

    private void saveLastUser(String username) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        prefs.put("lastLoggedInUser", username);
    }

    private void clearLastUser() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        prefs.remove("lastLoggedInUser");
    }

    @FXML
    private void handleShowRegister() throws IOException {
        SceneManager.showRegisterView();
    }
}