package org.example.demo;

import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
    private String id;
    private String type; // "income" or "expense"
    private double amount;
    private String category;
    private LocalDate date;
    private String description;
    private boolean isImportant;
    private String notes;

    public Transaction(String id, String type, double amount, String category,
                       LocalDate date, String description, boolean isImportant, String notes) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.isImportant = isImportant;
        this.notes = notes;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}