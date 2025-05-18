package org.example.finapp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.example.finapp.models.Transaction;
import org.example.finapp.services.AuthService;
import org.example.finapp.services.FinancialService;
import org.example.finapp.utils.AlertManager;
import org.example.finapp.utils.SceneManager;

import java.io.IOException;
import java.time.LocalDate;

public class MainController {
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField amountField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Transaction.TransactionType> typeComboBox;

    @FXML
    private Label totalIncomeLabel;
    @FXML
    private Label totalExpenseLabel;
    @FXML
    private Label balanceLabel;

    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, String> idColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, String> amountColumn;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> typeColumn;

    private FinancialService financialService = new FinancialService();
    private ObservableList<Transaction> transactionsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inisialisasi ComboBox (Pemasukan/Pengeluaran)
        typeComboBox.setItems(FXCollections.observableArrayList(
                Transaction.TransactionType.INCOME,
                Transaction.TransactionType.EXPENSE
        ));
        typeComboBox.setConverter(new StringConverter<Transaction.TransactionType>() {
            @Override
            public String toString(Transaction.TransactionType type) {
                return type == null ? "" : type.getDisplayName();
            }

            @Override
            public Transaction.TransactionType fromString(String string) {
                return null;
            }
        });

        // Set tanggal default (hari ini)
        datePicker.setValue(LocalDate.now());

        // Inisialisasi TableView
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());

        // Muat data awal
        refreshData();
    }

    @FXML
    private void handleAddTransaction() {
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        LocalDate date = datePicker.getValue();
        Transaction.TransactionType type = typeComboBox.getValue();

        // Validasi input
        if (description.isEmpty() || amountText.isEmpty() || date == null || type == null) {
            showAlert("Error", "Harap isi semua field!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            financialService.addTransaction(description, amount, date, type);
            refreshData();
            clearForm();
        } catch (NumberFormatException e) {
            showAlert("Error", "Jumlah harus berupa angka!");
        }
    }

    private void refreshData() {
        transactionsData.setAll(financialService.getAllTransactions());
        transactionsTable.setItems(transactionsData);

        // Update ringkasan keuangan
        totalIncomeLabel.setText(String.format("Rp%,.2f", financialService.getTotalIncome()));
        totalExpenseLabel.setText(String.format("Rp%,.2f", financialService.getTotalExpense()));
        balanceLabel.setText(String.format("Rp%,.2f", financialService.getBalance()));
    }

    private void clearForm() {
        descriptionField.clear();
        amountField.clear();
        datePicker.setValue(LocalDate.now());
        typeComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Tambahkan method ini di MainController
    @FXML
    private void handleLogout() {
        AuthService authService = new AuthService();
        authService.logout();
        try {
            SceneManager.showLoginView();
        } catch (IOException e) {
            AlertManager.showError("Error", "Gagal kembali ke halaman login");
        }
    }
}