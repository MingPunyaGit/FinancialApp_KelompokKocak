package org.example.finapp.services;

import org.example.finapp.models.Transaction;
import org.example.finapp.models.User;
import org.example.finapp.utils.FileUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FinancialService {
    private List<Transaction> transactions;
    private final String dataFilename;

    public FinancialService(User currentUser) {
        if (currentUser == null) {
            throw new IllegalStateException("Tidak ada pengguna yang login untuk FinancialService.");
        }
        this.dataFilename = currentUser.getUsername() + "_transactions.dat";
        loadTransactions();
    }

    // PERUBAHAN: Metode addTransaction diperbaiki
    public void addTransaction(String description, double amount, LocalDate date, Transaction.TransactionType type,
                               boolean important, String notes) {
        String id = UUID.randomUUID().toString();
        Transaction newTransaction = new Transaction(id, description, amount, date, type, important, notes);
        this.transactions.add(newTransaction);
        saveTransactions();
    }

    // PERUBAHAN: Metode updateTransaction diperbaiki
    public boolean updateTransaction(String id, String description, double amount, LocalDate date,
                                     Transaction.TransactionType type, boolean important, String notes) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId().equals(id)) {
                Transaction updatedTransaction = new Transaction(id, description, amount, date, type, important, notes);
                transactions.set(i, updatedTransaction);
                saveTransactions();
                return true;
            }
        }
        return false;
    }

    public boolean deleteTransaction(String id) {
        boolean removed = transactions.removeIf(t -> t.getId().equals(id));
        if (removed) {
            saveTransactions();
        }
        return removed;
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    // PERUBAHAN: getTotalIncome dan getTotalExpense diperbaiki untuk menggunakan enum
    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.PEMASUKAN)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.PENGELUARAN)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    // PERUBAHAN: Logika simpan dan muat data dipindahkan ke sini
    private void saveTransactions() {
        FileUtils.saveTransactions(dataFilename, this.transactions);
    }

    private void loadTransactions() {
        List<Transaction> loaded = FileUtils.loadTransactions(dataFilename);
        this.transactions = (loaded != null) ? loaded : new ArrayList<>();
    }
}