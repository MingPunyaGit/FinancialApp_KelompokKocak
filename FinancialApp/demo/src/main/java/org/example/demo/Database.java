package org.example.demo.models;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private static final String USERS_FILE = "users.dat";
    private static final String TRANSACTIONS_FILE_PREFIX = "transactions_";

    private Map<String, User> users;
    private Map<String, List<Transaction>> userTransactions;

    public Database() {
        users = new HashMap<>();
        userTransactions = new HashMap<>();
        loadData();
    }

    private void loadData() {
        loadUsers();
    }

    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found, will be created on first save");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadUserTransactions(String username) {
        String filename = TRANSACTIONS_FILE_PREFIX + username + ".dat";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            List<Transaction> transactions = (List<Transaction>) ois.readObject();
            userTransactions.put(username, transactions);
        } catch (FileNotFoundException e) {
            System.out.println("Transactions file not found for user " + username);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        saveUsers();
        for (String username : userTransactions.keySet()) {
            saveUserTransactions(username);
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUserTransactions(String username) {
        String filename = TRANSACTIONS_FILE_PREFIX + username + ".dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(userTransactions.get(username));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // User methods
    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, new User(username, password));
        userTransactions.put(username, new ArrayList<>());
        saveUsers();
        return true;
    }

    public boolean authenticateUser(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    // Transaction methods
    public List<Transaction> getUserTransactions(String username) {
        if (!userTransactions.containsKey(username)) {
            loadUserTransactions(username);
        }
        return userTransactions.getOrDefault(username, new ArrayList<>());
    }

    public void addTransaction(String username, Transaction transaction) {
        List<Transaction> transactions = getUserTransactions(username);
        transactions.add(transaction);
        userTransactions.put(username, transactions);
        saveUserTransactions(username);
    }

    public void updateTransaction(String username, String transactionId, Transaction updatedTransaction) {
        List<Transaction> transactions = getUserTransactions(username);
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId().equals(transactionId)) {
                transactions.set(i, updatedTransaction);
                break;
            }
        }
        userTransactions.put(username, transactions);
        saveUserTransactions(username);
    }

    public void deleteTransaction(String username, String transactionId) {
        List<Transaction> transactions = getUserTransactions(username);
        transactions.removeIf(t -> t.getId().equals(transactionId));
        userTransactions.put(username, transactions);
        saveUserTransactions(username);
    }
}