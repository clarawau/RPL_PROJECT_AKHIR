package org.Project.DataBase;

import java.sql.*;

public class JdbcDao {

    private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/database.db";


    public JdbcDao() {
        createUsersTableIfNotExists();
    }

    private Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
            return null;
        }
    }

    public void createUsersTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "emailId TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL" +
                ");";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabel users dicek/dibuat.");
        } catch (SQLException e) {
            System.out.println("Gagal membuat tabel: " + e.getMessage());
        }
    }

    public void insertRecord(String emailId, String password) {
        if (isEmailExist(emailId)) {
            System.out.println("Email sudah terdaftar!");
            return; // Jika email sudah ada, berhenti
        }

        String sql = "INSERT INTO users(emailId, password) VALUES(?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emailId);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Registrasi berhasil!");
        } catch (SQLException e) {
            System.out.println("Registrasi gagal: " + e.getMessage());
        }
    }

    public boolean isEmailExist(String emailId) {
        String sql = "SELECT * FROM users WHERE emailId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emailId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Cek email gagal: " + e.getMessage());
            return false;
        }
    }

    public boolean loginUser(String emailId, String password) {
        String sql = "SELECT * FROM users WHERE emailId = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emailId);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Login gagal: " + e.getMessage());
            return false;
        }
    }
}
