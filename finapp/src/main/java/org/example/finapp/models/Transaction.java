package org.example.finapp.models;

import java.time.LocalDate;

public class Transaction {
    public enum TransactionType { PEMASUKAN, PENGELUARAN }

    private String id;
    private String description;
    private double amount;
    private LocalDate date;
    private TransactionType type;
    private Category category;
    private boolean important;
    private String note;

    public Transaction(String id, String description, double amount, LocalDate date, TransactionType type, Category category, boolean important, String note) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
        this.important = important;
        this.note = note;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public TransactionType getType() { return type; }
    public Category getCategory() { return category; }
    public boolean isImportant() { return important; }
    public String getNote() { return note; }
}