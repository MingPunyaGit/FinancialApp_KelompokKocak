package org.example.finapp.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:finapp.db";

    public static Connection connect() throws SQLException {
        // System.out.println("[DB_LOG] Mencoba menghubungkan ke: " + DB_URL);
        Connection conn = DriverManager.getConnection(DB_URL);
        // System.out.println("[DB_LOG] Koneksi database berhasil.");
        return conn;
    }

    public static void initializeDatabase() {
        // System.out.println("[DB_LOG] Memulai inisialisasi database...");
        String userTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " username TEXT NOT NULL UNIQUE,"
                + " password TEXT NOT NULL,"
                + " full_name TEXT NOT NULL"
                + ");";

        String categoryTableSql = "CREATE TABLE IF NOT EXISTS categories ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT NOT NULL,"
                + " type TEXT NOT NULL CHECK(type IN ('PEMASUKAN', 'PENGELUARAN')),"
                + " UNIQUE(name, type)"
                + ");";

        String transactionTableSql = "CREATE TABLE IF NOT EXISTS transactions ("
                + " id TEXT PRIMARY KEY,"
                + " description TEXT NOT NULL,"
                + " amount REAL NOT NULL,"
                + " date TEXT NOT NULL,"
                + " type TEXT NOT NULL CHECK(type IN ('PEMASUKAN', 'PENGELUARAN')),"
                + " is_important INTEGER NOT NULL,"
                + " note TEXT,"
                + " category_id INTEGER NOT NULL,"
                + " user_id INTEGER NOT NULL,"
                + " FOREIGN KEY (category_id) REFERENCES categories(id),"
                + " FOREIGN KEY (user_id) REFERENCES users(id)"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // System.out.println("[DB_LOG] Koneksi untuk inisialisasi berhasil.");
            stmt.execute(userTableSql);
            // System.out.println("[DB_LOG] Tabel users dibuat/diverifikasi.");
            stmt.execute(categoryTableSql);
            // System.out.println("[DB_LOG] Tabel categories dibuat/diverifikasi.");
            stmt.execute(transactionTableSql);
            // System.out.println("[DB_LOG] Tabel transactions dibuat/diverifikasi.");
            insertDefaultCategories(conn);
        } catch (SQLException e) {
            System.err.println("[DB_ERROR] Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
        // System.out.println("[DB_LOG] Inisialisasi database selesai.");
    }

    private static void insertDefaultCategories(Connection conn) throws SQLException {
        try (Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM categories")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Kategori sudah ada
            }
        }

        System.out.println("Inserting default categories..."); // Biarkan log ini untuk konfirmasi
        String insertSql = "INSERT INTO categories(name, type) VALUES(?, ?)";
        try (var pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, "Gaji"); pstmt.setString(2, "PEMASUKAN"); pstmt.addBatch();
            pstmt.setString(1, "Bonus"); pstmt.setString(2, "PEMASUKAN"); pstmt.addBatch();
            pstmt.setString(1, "Investasi"); pstmt.setString(2, "PEMASUKAN"); pstmt.addBatch();
            pstmt.setString(1, "Lain-lain (Pemasukan)"); pstmt.setString(2, "PEMASUKAN"); pstmt.addBatch();

            pstmt.setString(1, "Makanan & Minuman"); pstmt.setString(2, "PENGELUARAN"); pstmt.addBatch();
            pstmt.setString(1, "Transportasi"); pstmt.setString(2, "PENGELUARAN"); pstmt.addBatch();
            pstmt.setString(1, "Tagihan"); pstmt.setString(2, "PENGELUARAN"); pstmt.addBatch();
            pstmt.setString(1, "Hiburan"); pstmt.setString(2, "PENGELUARAN"); pstmt.addBatch();
            pstmt.setString(1, "Belanja"); pstmt.setString(2, "PENGELUARAN"); pstmt.addBatch();
            pstmt.setString(1, "Pendidikan"); pstmt.setString(2, "PENGELUARAN"); pstmt.addBatch();
            pstmt.setString(1, "Kesehatan"); pstmt.setString(2, "PENGELUARAN"); pstmt.addBatch();
            pstmt.setString(1, "Lain-lain (Pengeluaran)"); pstmt.setString(2, "PENGELUARAN"); pstmt.addBatch();

            pstmt.executeBatch();
        }
    }
}