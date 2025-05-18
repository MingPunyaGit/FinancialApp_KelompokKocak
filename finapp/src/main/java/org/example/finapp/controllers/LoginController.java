package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.finapp.services.AuthService;
import org.example.finapp.utils.AlertManager;
import org.example.finapp.utils.SceneManager;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authService.login(username, password)) {
            // Pindah ke main view
            SceneManager.showMainView();
        } else {
            AlertManager.showError("Login Gagal", "Username atau password salah");
        }
    }
}