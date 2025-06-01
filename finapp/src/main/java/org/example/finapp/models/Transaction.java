package org.example.finapp.models;

import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 2L;

    public enum TransactionType {
        PEMASUKAN,
        PENGELUARAN
    }

    private String id;
    private String description;
    private double amount;
    private LocalDate date;
    private TransactionType type;
    private boolean important;
    private String note;

    public Transaction(String id, String description, double amount, LocalDate date, TransactionType type, boolean important, String note) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.important = important;
        this.note = note;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public boolean isImportant() { return important; }
    public void setImportant(boolean important) { this.important = important; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}