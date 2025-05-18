package org.example.finapp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.example.finapp.models.Transaction;
import org.example.finapp.services.AuthService;
import org.example.finapp.services.FinancialService;
import org.example.finapp.utils.AlertManager;
import org.example.finapp.utils.SceneManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

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
    @FXML
    private TableColumn<Transaction, Void> actionColumn;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;

    private FinancialService financialService = new FinancialService();
    private ObservableList<Transaction> transactionsData = FXCollections.observableArrayList();
    private Transaction selectedTransaction = null;
    private boolean isEditMode = false;

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

        // Setup Action Column dengan tombol Edit dan Delete
        setupActionColumn();

        // Setup mode awal
        setAddMode();

        // Muat data awal
        refreshData();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<Transaction, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");

            {
                editBtn.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    editTransaction(transaction);
                });

                deleteBtn.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    deleteTransaction(transaction);
                });

                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 8 2 8;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 8 2 8;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    var hbox = new javafx.scene.layout.HBox(5);
                    hbox.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(hbox);
                }
            }
        });
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

            if (isEditMode && selectedTransaction != null) {
                // Update transaksi yang sudah ada
                financialService.updateTransaction(selectedTransaction.getId(), description, amount, date, type);
                showAlert("Sukses", "Transaksi berhasil diperbarui!", Alert.AlertType.INFORMATION);
                setAddMode();
            } else {
                // Tambah transaksi baru
                financialService.addTransaction(description, amount, date, type);
                showAlert("Sukses", "Transaksi berhasil ditambahkan!", Alert.AlertType.INFORMATION);
            }

            refreshData();
            clearForm();
        } catch (NumberFormatException e) {
            showAlert("Error", "Jumlah harus berupa angka!");
        }
    }

    @FXML
    private void handleCancel() {
        setAddMode();
        clearForm();
    }

    private void editTransaction(Transaction transaction) {
        selectedTransaction = transaction;
        isEditMode = true;

        // Isi form dengan data transaksi yang dipilih
        descriptionField.setText(transaction.getDescription());
        amountField.setText(String.valueOf(transaction.getAmount()));
        datePicker.setValue(transaction.getDate());
        typeComboBox.setValue(transaction.getType());

        // Ubah tampilan tombol
        addButton.setText("Update");
        updateButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    private void deleteTransaction(Transaction transaction) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Transaksi");
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus transaksi ini?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (financialService.deleteTransaction(transaction.getId())) {
                showAlert("Sukses", "Transaksi berhasil dihapus!", Alert.AlertType.INFORMATION);
                refreshData();

                // Jika transaksi yang dihapus sedang diedit, reset form
                if (isEditMode && selectedTransaction != null &&
                        selectedTransaction.getId().equals(transaction.getId())) {
                    setAddMode();
                    clearForm();
                }
            } else {
                showAlert("Error", "Gagal menghapus transaksi!");
            }
        }
    }

    private void setAddMode() {
        isEditMode = false;
        selectedTransaction = null;
        addButton.setText("Tambah");
        updateButton.setVisible(false);
        cancelButton.setVisible(false);
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
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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