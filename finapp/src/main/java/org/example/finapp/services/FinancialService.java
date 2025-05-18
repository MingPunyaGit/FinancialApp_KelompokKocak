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
}