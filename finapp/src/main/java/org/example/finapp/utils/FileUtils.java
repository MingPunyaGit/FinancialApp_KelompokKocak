package org.example.finapp.utils;

import org.example.finapp.models.Transaction;

import java.io.*;
import java.util.List;

public class FileUtils {
    public static void saveTransactions(String filename, List<Transaction> transactions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            e.printStackTrace();
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
            return null;
        }
    }
}