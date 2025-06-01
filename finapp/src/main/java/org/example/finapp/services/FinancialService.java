package org.example.finapp.services;

import javafx.scene.chart.XYChart;
import org.example.finapp.models.Category;
import org.example.finapp.models.Transaction;
import org.example.finapp.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class FinancialService {
    private final User currentUser;

    public FinancialService(User currentUser) {
        if (currentUser == null) throw new IllegalStateException("Current user cannot be null for FinancialService");
        this.currentUser = currentUser;
    }

    // --- METODE KATEGORI ---
    public List<Category> getCategories(Transaction.TransactionType type) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE type = ? ORDER BY name ASC";
        try (Connection conn = DatabaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name"), type));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // --- METODE TRANSAKSI (CRUD) ---
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.id as t_id, t.description, t.amount, t.date, t.type as transaction_type, t.is_important, t.note, " +
                "c.id as c_id, c.name as category_name, c.type as category_type " +
                "FROM transactions t " +
                "JOIN categories c ON t.category_id = c.id " +
                "WHERE t.user_id = ? ORDER BY t.date DESC, t.id DESC";
        try (Connection conn = DatabaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Category cat = new Category(rs.getInt("c_id"), rs.getString("category_name"), Transaction.TransactionType.valueOf(rs.getString("category_type")));
                transactions.add(new Transaction(
                        rs.getString("t_id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("date")),
                        Transaction.TransactionType.valueOf(rs.getString("transaction_type")),
                        cat,
                        rs.getInt("is_important") == 1,
                        rs.getString("note")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public void addTransaction(String description, double amount, LocalDate date, Transaction.TransactionType type, Category category, boolean important, String note) {
        String sql = "INSERT INTO transactions(id, description, amount, date, type, is_important, note, category_id, user_id) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseService.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, description);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, date.toString());
            pstmt.setString(5, type.toString());
            pstmt.setInt(6, important ? 1 : 0);
            pstmt.setString(7, note);
            pstmt.setInt(8, category.getId());
            pstmt.setInt(9, currentUser.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTransaction(String id, String description, double amount, LocalDate date, Transaction.TransactionType type, Category category, boolean important, String note) {
        String sql = "UPDATE transactions SET description=?, amount=?, date=?, type=?, is_important=?, note=?, category_id=? WHERE id=? AND user_id=?";
        try (Connection conn = DatabaseService.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, date.toString());
            pstmt.setString(4, type.toString());
            pstmt.setInt(5, important ? 1 : 0);
            pstmt.setString(6, note);
            pstmt.setInt(7, category.getId());
            pstmt.setString(8, id);
            pstmt.setInt(9, currentUser.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTransaction(String id) {
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseService.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setInt(2, currentUser.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- METODE KALKULASI & GRAFIK ---
    private double calculateTotal(Transaction.TransactionType type) {
        String sql = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = ?";
        try (Connection conn = DatabaseService.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            pstmt.setString(2, type.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalIncome() { return calculateTotal(Transaction.TransactionType.PEMASUKAN); }
    public double getTotalExpense() { return calculateTotal(Transaction.TransactionType.PENGELUARAN); }
    public double getBalance() { return getTotalIncome() - getTotalExpense(); }

    public Map<String, Double> getExpenseByCategory() {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = "SELECT c.name as category_name, SUM(t.amount) as total_amount " +
                "FROM transactions t JOIN categories c ON t.category_id = c.id " +
                "WHERE t.user_id = ? AND t.type = 'PENGELUARAN' " +
                "GROUP BY c.name ORDER BY total_amount DESC";
        try (Connection conn = DatabaseService.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("category_name"), rs.getDouble("total_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public Map<Month, double[]> getMonthlySummary(int year) {
        Map<Month, double[]> summary = new TreeMap<>();
        for (Month month : Month.values()) {
            summary.put(month, new double[]{0.0, 0.0});
        }
        String sql = "SELECT strftime('%m', date) as month_num, type, SUM(amount) as total_amount " +
                "FROM transactions WHERE user_id = ? AND strftime('%Y', date) = ? " +
                "GROUP BY month_num, type";
        try (Connection conn = DatabaseService.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            pstmt.setString(2, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Month month = Month.of(Integer.parseInt(rs.getString("month_num")));
                Transaction.TransactionType type = Transaction.TransactionType.valueOf(rs.getString("type"));
                double total = rs.getDouble("total_amount");
                if (type == Transaction.TransactionType.PEMASUKAN) {
                    summary.get(month)[0] = total;
                } else {
                    summary.get(month)[1] = total;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }

    public List<XYChart.Data<String, Number>> getBalanceTrend() {
        List<Transaction> transactionsForTrend = new ArrayList<>();
        String sql = "SELECT t.id as t_id, t.description, t.amount, t.date, t.type as transaction_type, t.is_important, t.note, " +
                "c.id as c_id, c.name as category_name, c.type as category_type " +
                "FROM transactions t JOIN categories c ON t.category_id = c.id " +
                "WHERE t.user_id = ? ORDER BY t.date ASC, t.id ASC";

        try (Connection conn = DatabaseService.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Category cat = new Category(rs.getInt("c_id"), rs.getString("category_name"), Transaction.TransactionType.valueOf(rs.getString("category_type")));
                transactionsForTrend.add(new Transaction(
                        rs.getString("t_id"), rs.getString("description"), rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("date")), Transaction.TransactionType.valueOf(rs.getString("transaction_type")),
                        cat, rs.getInt("is_important") == 1, rs.getString("note")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<XYChart.Data<String, Number>> trendData = new ArrayList<>();
        double runningBalance = 0.0;
        for (Transaction t : transactionsForTrend) {
            runningBalance += (t.getType() == Transaction.TransactionType.PEMASUKAN) ? t.getAmount() : -t.getAmount();
            trendData.add(new XYChart.Data<>(t.getDate().toString() + " (" + t.getDescription().substring(0, Math.min(t.getDescription().length(), 10)) + ")", runningBalance));
        }
        return trendData;
    }

    /**
     * Mengambil semua transaksi yang ditandai penting untuk pengguna saat ini.
     * Diurutkan berdasarkan tanggal terbaru.
     * @return List dari objek Transaction yang penting.
     */
    public List<Transaction> getImportantTransactions() {
        List<Transaction> importantTransactions = new ArrayList<>();
        String sql = "SELECT t.id as t_id, t.description, t.amount, t.date, t.type as transaction_type, t.is_important, t.note, " +
                "c.id as c_id, c.name as category_name, c.type as category_type " +
                "FROM transactions t " +
                "JOIN categories c ON t.category_id = c.id " +
                "WHERE t.user_id = ? AND t.is_important = 1 " +
                "ORDER BY t.date DESC, t.id DESC";

        try (Connection conn = DatabaseService.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.currentUser.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Category cat = new Category(
                        rs.getInt("c_id"),
                        rs.getString("category_name"),
                        Transaction.TransactionType.valueOf(rs.getString("category_type"))
                );
                importantTransactions.add(new Transaction(
                        rs.getString("t_id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("date")),
                        Transaction.TransactionType.valueOf(rs.getString("transaction_type")),
                        cat,
                        rs.getInt("is_important") == 1,
                        rs.getString("note")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return importantTransactions;
    }
}