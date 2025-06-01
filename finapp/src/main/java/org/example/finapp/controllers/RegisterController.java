package org.example.finapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.finapp.services.AuthService;
import org.example.finapp.utils.AlertManager;
import org.example.finapp.utils.SceneManager;

import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegister() throws IOException {
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            AlertManager.showError("Registrasi Gagal", "Semua kolom harus diisi.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            AlertManager.showError("Registrasi Gagal", "Password dan Konfirmasi Password tidak cocok.");
            return;
        }

        if (authService.register(username, password, fullName)) {
            AlertManager.showInfo("Registrasi Berhasil", "Akun berhasil dibuat. Silakan masuk.");
            SceneManager.showLoginView();
        } else {
            AlertManager.showError("Registrasi Gagal", "Username '" + username + "' sudah digunakan.");
        }
    }

    @FXML
    private void handleShowLogin() throws IOException {
        SceneManager.showLoginView();
    }
}