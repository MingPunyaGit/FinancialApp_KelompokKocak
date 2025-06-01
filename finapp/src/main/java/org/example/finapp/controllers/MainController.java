package org.example.finapp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.finapp.models.Transaction;
import org.example.finapp.services.AuthService;
import org.example.finapp.services.FinancialService;
import org.example.finapp.utils.AlertManager;
import org.example.finapp.utils.SceneManager;

import java.io.IOException;
import java.time.LocalDate;

public class MainController {
    @FXML private TextField descriptionField, amountField, noteField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private CheckBox importantCheckBox;
    @FXML private Label totalIncomeLabel, totalExpenseLabel, balanceLabel;
    @FXML private Button addButton, updateButton, cancelButton;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> idColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, LocalDate> dateColumn;
    @FXML private TableColumn<Transaction, Transaction.TransactionType> typeColumn;
    @FXML private TableColumn<Transaction, Boolean> importantColumn;
    @FXML private TableColumn<Transaction, String> noteColumn;
    @FXML private TableColumn<Transaction, Void> actionColumn;
    @FXML private PieChart transactionsPieChart;

    private FinancialService financialService;
    private final AuthService authService = new AuthService();
    private final ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();
    private Transaction selectedTransaction = null;

    @FXML
    public void initialize() {
        try {
            this.financialService = new FinancialService(authService.getCurrentUser());
        } catch (IllegalStateException e) {
            handleLogoutError();
            return;
        }

        setupUIComponents();
        refreshData();
    }

    private void setupUIComponents() {
        typeComboBox.setItems(FXCollections.observableArrayList(
                Transaction.TransactionType.PEMASUKAN.toString(),
                Transaction.TransactionType.PENGELUARAN.toString()
        ));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        importantColumn.setCellValueFactory(new PropertyValueFactory<>("important"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        addActionButtonsToTable();
        transactionsTable.setItems(transactionsList);
        transactionsPieChart.setLegendVisible(true);
    }

    private void refreshData() {
        transactionsList.setAll(financialService.getAllTransactions());
        updateSummary();
        transactionsTable.refresh();
    }

    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Hapus");
            private final HBox pane = new HBox(5, editButton, deleteButton);
            {
                editButton.setOnAction(event -> {
                    selectedTransaction = getTableView().getItems().get(getIndex());
                    populateForm(selectedTransaction);
                });
                deleteButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    financialService.deleteTransaction(transaction.getId());
                    refreshData();
                    clearForm();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void populateForm(Transaction tx) {
        descriptionField.setText(tx.getDescription());
        amountField.setText(String.valueOf(tx.getAmount()));
        datePicker.setValue(tx.getDate());
        typeComboBox.setValue(tx.getType().toString());
        importantCheckBox.setSelected(tx.isImportant());
        noteField.setText(tx.getNote());
        showUpdateState();
    }

    @FXML
    private void handleAddTransaction() {
        if (!validateInput()) return;

        financialService.addTransaction(
                descriptionField.getText(),
                Double.parseDouble(amountField.getText()),
                datePicker.getValue(),
                Transaction.TransactionType.valueOf(typeComboBox.getValue()),
                importantCheckBox.isSelected(),
                noteField.getText()
        );

        refreshData();
        clearForm();
    }

    @FXML
    private void handleUpdateTransaction() {
        if (selectedTransaction == null || !validateInput()) return;

        financialService.updateTransaction(
                selectedTransaction.getId(),
                descriptionField.getText(),
                Double.parseDouble(amountField.getText()),
                datePicker.getValue(),
                Transaction.TransactionType.valueOf(typeComboBox.getValue()),
                importantCheckBox.isSelected(),
                noteField.getText()
        );

        refreshData();
        clearForm();
        showAddState();
    }

    private void updateSummary() {
        double totalIncome = financialService.getTotalIncome();
        double totalExpense = financialService.getTotalExpense();
        double balance = financialService.getBalance();

        totalIncomeLabel.setText(formatRupiah(totalIncome));
        totalExpenseLabel.setText(formatRupiah(totalExpense));
        balanceLabel.setText(formatRupiah(balance));

        updatePieChart(totalIncome, totalExpense);
    }

    @FXML private void handleCancel() { clearForm(); showAddState(); }
    private void showUpdateState() { addButton.setVisible(false); updateButton.setVisible(true); cancelButton.setVisible(true); }
    private void showAddState() { addButton.setVisible(true); updateButton.setVisible(false); cancelButton.setVisible(false); selectedTransaction = null; }

    @FXML
    private void handleLogout() {
        authService.logout();
        try { SceneManager.showLoginView(); } catch (IOException e) { e.printStackTrace(); }
    }

    private void handleLogoutError() {
        try { AlertManager.showError("Error", "Sesi tidak ditemukan."); SceneManager.showLoginView(); } catch (IOException e) { e.printStackTrace(); }
    }

    private void clearForm() {
        descriptionField.clear(); amountField.clear(); datePicker.setValue(null);
        typeComboBox.setValue(null); importantCheckBox.setSelected(false); noteField.clear();
    }

    private boolean validateInput() {
        String error = "";
        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) error += "Keterangan wajib diisi.\n";
        if (amountField.getText() == null || amountField.getText().trim().isEmpty()) error += "Jumlah wajib diisi.\n";
        else {
            try { if (Double.parseDouble(amountField.getText()) <= 0) error += "Jumlah harus lebih dari 0.\n"; }
            catch (NumberFormatException e) { error += "Jumlah harus angka valid.\n"; }
        }
        if (datePicker.getValue() == null) error += "Tanggal wajib dipilih.\n";
        if (typeComboBox.getValue() == null) error += "Jenis transaksi wajib dipilih.\n";

        if (!error.isEmpty()) { AlertManager.showError("Validasi Gagal", error); return false; }
        return true;
    }

    private void updatePieChart(double income, double expense) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        if (income > 0) pieChartData.add(new PieChart.Data("PEMASUKAN", income));
        if (expense > 0) pieChartData.add(new PieChart.Data("PENGELUARAN", expense));
        transactionsPieChart.setData(pieChartData);
    }

    private String formatRupiah(double amount) { return String.format("Rp %,.0f", amount).replace(',', '.'); }
}