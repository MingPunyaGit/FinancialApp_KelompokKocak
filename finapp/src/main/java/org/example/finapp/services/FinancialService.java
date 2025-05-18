package org.example.finapp.services;

import org.example.finapp.models.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FinancialService {
    private List<Transaction> transactions = new ArrayList<>();

    // Menambahkan transaksi baru
    public void addTransaction(String description, double amount, LocalDate date, Transaction.TransactionType type) {
        String id = UUID.randomUUID().toString();
        transactions.add(new Transaction(id, description, amount, date, type));
    }

    // Update transaksi yang sudah ada
    public boolean updateTransaction(String id, String description, double amount, LocalDate date, Transaction.TransactionType type) {
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            if (transaction.getId().equals(id)) {
                // Ganti dengan transaksi yang baru
                transactions.set(i, new Transaction(id, description, amount, date, type));
                return true;
            }
        }
        return false;
    }

    // Menghapus transaksi
    public boolean deleteTransaction(String id) {
        return transactions.removeIf(transaction -> transaction.getId().equals(id));
    }

    // Mencari transaksi berdasarkan ID
    public Transaction getTransactionById(String id) {
        return transactions.stream()
                .filter(transaction -> transaction.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Mengambil semua transaksi
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    // Menghitung total pemasukan
    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Menghitung total pengeluaran
    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Menghitung saldo (Pemasukan - Pengeluaran)
    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    // Method untuk mendapatkan transaksi berdasarkan rentang tanggal
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> (t.getDate().isEqual(startDate) || t.getDate().isAfter(startDate)) &&
                        (t.getDate().isEqual(endDate) || t.getDate().isBefore(endDate)))
                .toList();
    }

    // Method untuk mendapatkan transaksi berdasarkan tipe
    public List<Transaction> getTransactionsByType(Transaction.TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .toList();
    }
}