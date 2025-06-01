package org.example.finapp.models;

import java.util.Objects;

public class Category {
    private int id;
    private String name;
    private Transaction.TransactionType type;

    public Category(int id, String name, Transaction.TransactionType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Transaction.TransactionType getType() { return type; }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id && Objects.equals(name, category.name) && type == category.type;
    }

    @Override
    public int hashCode() { return Objects.hash(id, name, type); }
}