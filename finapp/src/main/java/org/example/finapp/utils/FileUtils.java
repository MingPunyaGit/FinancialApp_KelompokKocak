// File: src/main/java/org/example/finapp/utils/FileUtils.java
package org.example.finapp.utils;

import org.example.finapp.models.Category;
import org.example.finapp.models.Transaction;

import java.io.*;
import java.util.List;

public class FileUtils {
    public static void saveTransactions(String filename, List<Transaction> transactions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            e.printStackTrace();
            AlertManager.showError("Gagal Menyimpan", "Tidak dapat menyimpan data transaksi ke file.");
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Transaction> loadTransactions(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Transaction>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            AlertManager.showError("Gagal Memuat", "Gagal membaca data transaksi dari file. Mungkin file korup.");
            return null;
        }
    }

    // --- PERUBAHAN BARU ---
    public static void saveCategories(String filename, List<Category> categories) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(categories);
        } catch (IOException e) {
            e.printStackTrace();
            AlertManager.showError("Gagal Menyimpan Kategori", "Tidak dapat menyimpan data kategori.");
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Category> loadCategories(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Category>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}