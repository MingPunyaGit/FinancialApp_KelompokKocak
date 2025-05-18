package org.example.finapp.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Transaction {
    private String id;
    private String description;
    private double amount;
    private LocalDate date;
    private TransactionType type;

    public Transaction(String id, String description, double amount, LocalDate date, TransactionType type) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    // JavaFX Property Methods (untuk TableView binding)
    public StringProperty idProperty() {
        return new SimpleStringProperty(id);
    }

    public StringProperty descriptionProperty() {
        return new SimpleStringProperty(description);
    }

    public StringProperty amountProperty() {
        return new SimpleStringProperty(String.format("Rp%,.2f", amount));
    }

    public StringProperty dateProperty() {
        return new SimpleStringProperty(date.toString());
    }

    public StringProperty typeProperty() {
        return new SimpleStringProperty(type.getDisplayName());
    }

    // Enum untuk jenis transaksi (Pemasukan/Pengeluaran)
    public enum TransactionType {
        INCOME("Pemasukan"),
        EXPENSE("Pengeluaran");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}