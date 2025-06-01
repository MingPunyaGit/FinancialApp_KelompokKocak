package org.example.finapp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.finapp.Main; // <-- Pastikan import Main ada
import org.example.finapp.models.Category;
import org.example.finapp.models.Transaction;
import org.example.finapp.services.AuthService;
import org.example.finapp.services.FinancialService;
import org.example.finapp.utils.AlertManager;
import org.example.finapp.utils.SceneManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences; // <-- Pastikan import Preferences ada
import java.util.stream.Collectors;

public class MainController {
    @FXML private TextField descriptionField, amountField, noteField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private CheckBox importantCheckBox;
    @FXML private Label totalIncomeLabel, totalExpenseLabel, balanceLabel;
    @FXML private Button addButton, updateButton, cancelButton;
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> idColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, LocalDate> dateColumn;
    @FXML private TableColumn<Transaction, Transaction.TransactionType> typeColumn;
    @FXML private TableColumn<Transaction, Category> categoryColumn;
    @FXML private TableColumn<Transaction, Boolean> importantColumn;
    @FXML private TableColumn<Transaction, String> noteColumn;
    @FXML private TableColumn<Transaction, Void> actionColumn;
    @FXML private PieChart transactionsPieChart;
    @FXML private BarChart<String, Number> monthlyBarChart;
    @FXML private LineChart<String, Number> balanceLineChart;
    @FXML private ListView<String> importantNotesListView; // Field baru

    private FinancialService financialService;
    private final AuthService authService = new AuthService();
    private final ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();
    private Transaction selectedTransaction = null;

    @FXML
    public void initialize() {
        try {
            if (authService.getCurrentUser() == null) {
                Preferences prefs = Preferences.userNodeForPackage(Main.class);
                String lastUser = prefs.get("lastLoggedInUser", null);
                if (lastUser == null || !authService.loginFromSession(lastUser)) {
                    throw new IllegalStateException("Sesi pengguna tidak ditemukan. Silakan login kembali.");
                }
            }
            this.financialService = new FinancialService(authService.getCurrentUser());
        } catch (IllegalStateException e) {
            handleLogoutError(e.getMessage());
            return;
        }
        setupUIComponents();
        refreshData();
    }

    private void setupUIComponents() {
        typeComboBox.setItems(FXCollections.observableArrayList(Transaction.TransactionType.PEMASUKAN.toString(), Transaction.TransactionType.PENGELUARAN.toString()));
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                Transaction.TransactionType selectedType = Transaction.TransactionType.valueOf(newValue);
                categoryComboBox.setItems(FXCollections.observableArrayList(financialService.getCategories(selectedType)));
                categoryComboBox.getSelectionModel().clearSelection();
                categoryComboBox.setDisable(false);
            } else {
                categoryComboBox.getItems().clear();
                categoryComboBox.setDisable(true);
            }
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        importantColumn.setCellValueFactory(new PropertyValueFactory<>("important"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        addActionButtonsToTable();
        transactionsTable.setItems(transactionsList);
    }

    private void refreshData() {
        if (financialService == null) return;
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
                    if (AlertManager.showConfirmation("Konfirmasi Hapus", "Apakah Anda yakin ingin menghapus transaksi ini?")) {
                        Transaction transaction = getTableView().getItems().get(getIndex());
                        financialService.deleteTransaction(transaction.getId());
                        refreshData();
                        clearForm();
                    }
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
        categoryComboBox.setValue(tx.getCategory());
        importantCheckBox.setSelected(tx.isImportant());
        noteField.setText(tx.getNote());
        showUpdateState();
    }

    @FXML private void handleAddTransaction() {
        if (!validateInput()) return;
        financialService.addTransaction(descriptionField.getText(), Double.parseDouble(amountField.getText()), datePicker.getValue(), Transaction.TransactionType.valueOf(typeComboBox.getValue()), categoryComboBox.getValue(), importantCheckBox.isSelected(), noteField.getText());
        refreshData();
        clearForm();
    }

    @FXML private void handleUpdateTransaction() {
        if (selectedTransaction == null || !validateInput()) return;
        financialService.updateTransaction(selectedTransaction.getId(), descriptionField.getText(), Double.parseDouble(amountField.getText()), datePicker.getValue(), Transaction.TransactionType.valueOf(typeComboBox.getValue()), categoryComboBox.getValue(), importantCheckBox.isSelected(), noteField.getText());
        refreshData();
        clearForm();
        showAddState();
    }

    private void updateSummary() {
        if (financialService == null) return;
        totalIncomeLabel.setText(formatRupiah(financialService.getTotalIncome()));
        totalExpenseLabel.setText(formatRupiah(financialService.getTotalExpense()));
        balanceLabel.setText(formatRupiah(financialService.getBalance()));
        updateCharts();
        updateImportantNotesList(); // Memanggil update daftar catatan penting
    }

    @FXML private void handleCancel() { clearForm(); showAddState(); }
    private void showUpdateState() { addButton.setVisible(false); updateButton.setVisible(true); cancelButton.setVisible(true); }
    private void showAddState() { addButton.setVisible(true); updateButton.setVisible(false); cancelButton.setVisible(false); selectedTransaction = null; }

    @FXML private void handleLogout() {
        authService.logout();
        clearLastUser();
        try { SceneManager.showLoginView(); } catch (IOException e) { e.printStackTrace(); }
    }

    private void clearLastUser() {
        Preferences prefs = Preferences.userNodeForPackage(org.example.finapp.Main.class);
        prefs.remove("lastLoggedInUser");
    }

    private void handleLogoutError(String message) {
        AlertManager.showError("Error Sesi", message);
        clearLastUser();
        try { SceneManager.showLoginView(); } catch (IOException e) { e.printStackTrace(); }
    }

    private void clearForm() {
        descriptionField.clear(); amountField.clear(); datePicker.setValue(null);
        typeComboBox.setValue(null); categoryComboBox.setValue(null); categoryComboBox.setDisable(true);
        importantCheckBox.setSelected(false); noteField.clear();
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
        if (categoryComboBox.getValue() == null) error += "Kategori transaksi wajib dipilih.\n";
        if (!error.isEmpty()) { AlertManager.showError("Validasi Gagal", error); return false; }
        return true;
    }

    private String formatRupiah(double amount) { return String.format(Locale.forLanguageTag("id-ID"), "Rp %,.0f", amount); }

    private void updateCharts() {
        if (financialService == null) return;
        updatePieChart();
        updateBarChart();
        updateLineChart();
    }

    private void updatePieChart() {
        transactionsPieChart.setTitle("Distribusi Pengeluaran per Kategori");
        Map<String, Double> expenseData = financialService.getExpenseByCategory();
        ObservableList<PieChart.Data> pieChartData = expenseData.entrySet().stream().map(entry -> new PieChart.Data(entry.getKey(), entry.getValue())).collect(Collectors.toCollection(FXCollections::observableArrayList));
        if (pieChartData.isEmpty()) { pieChartData.add(new PieChart.Data("Belum ada pengeluaran", 1)); }
        transactionsPieChart.setData(pieChartData);
    }

    private void updateBarChart() {
        int currentYear = LocalDate.now().getYear();
        monthlyBarChart.setTitle("Ringkasan Bulanan " + currentYear);
        Map<Month, double[]> summary = financialService.getMonthlySummary(currentYear);
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>(); incomeSeries.setName("Pemasukan");
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>(); expenseSeries.setName("Pengeluaran");
        Locale indonesia = new Locale("id");
        for (Map.Entry<Month, double[]> entry : summary.entrySet()) {
            String monthName = entry.getKey().getDisplayName(TextStyle.SHORT, indonesia);
            incomeSeries.getData().add(new XYChart.Data<>(monthName, entry.getValue()[0]));
            expenseSeries.getData().add(new XYChart.Data<>(monthName, entry.getValue()[1]));
        }
        monthlyBarChart.getData().setAll(incomeSeries, expenseSeries);
    }

    private void updateLineChart() {
        List<XYChart.Data<String, Number>> trendData = financialService.getBalanceTrend();
        XYChart.Series<String, Number> balanceSeries = new XYChart.Series<>(); balanceSeries.setName("Tren Saldo");
        balanceLineChart.getData().clear();
        if (!trendData.isEmpty()) {
            balanceSeries.getData().setAll(trendData);
            balanceLineChart.getData().add(balanceSeries);
        }
    }

    /**
     * Metode baru untuk mengambil dan menampilkan transaksi penting di ListView.
     */
    private void updateImportantNotesList() {
        if (financialService == null) {
            if (importantNotesListView != null) { // Cek null untuk importantNotesListView
                importantNotesListView.setItems(FXCollections.observableArrayList("Data layanan tidak tersedia."));
            }
            return;
        }

        List<Transaction> importantTx = financialService.getImportantTransactions();
        ObservableList<String> items = FXCollections.observableArrayList();

        if (importantTx.isEmpty()) {
            items.add("Tidak ada catatan penting.");
        } else {
            for (Transaction tx : importantTx) {
                String noteText = (tx.getNote() != null && !tx.getNote().trim().isEmpty()) ? " (" + tx.getNote().trim() + ")" : "";
                items.add(String.format("[%s] %s%s - %s",
                        tx.getDate().toString(),
                        tx.getDescription(),
                        noteText,
                        formatRupiah(tx.getAmount())
                ));
            }
        }
        if (importantNotesListView != null) { // Cek null sebelum setItems
            importantNotesListView.setItems(items);
        }
    }
}