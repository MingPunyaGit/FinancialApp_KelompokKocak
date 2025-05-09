package org.example.demo.controller;


import org.example.demo.models.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    private Database database = new Database();

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> handleLogin());
        registerButton.setOnAction(e -> handleRegister());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username dan password harus diisi");
            return;
        }

        if (database.authenticateUser(username, password)) {
            try {
                // Load dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/financialapp/views/dashboard.fxml"));
                Parent root = loader.load();

                DashboardController controller = loader.getController();
                controller.setUsername(username);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard - " + username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Username atau password salah");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username dan password harus diisi");
            return;
        }

        if (database.registerUser(username, password)) {
            messageLabel.setText("Registrasi berhasil! Silakan login");
        } else {
            messageLabel.setText("Username sudah digunakan");
        }
    }
}
