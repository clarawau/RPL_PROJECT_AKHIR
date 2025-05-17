package org.Project.DataBase;

import java.sql.*;

public class JdbcDao {

    private static final String DB_URL = "jdbc:sqlite:users.db";

    // Buat koneksi ke database SQLite
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Buat tabel jika belum ada (bisa dipanggil di awal)
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "pet TEXT NOT NULL," +
                "food TEXT NOT NULL," +
                "book TEXT NOT NULL," +
                "color TEXT NOT NULL" +
                ");";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean insertUser(String username, String password, String pet, String food, String book, String color) {
        String sql = "INSERT INTO users(username, password, pet, food, book, color) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, pet);
            stmt.setString(4, food);
            stmt.setString(5, book);
            stmt.setString(6, color);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public boolean isUsernameExist(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Verifikasi login
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Verifikasi jawaban security question
    public boolean verifySecurityAnswers(String username, String pet, String food, String book, String color) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND pet = ? AND food = ? AND book = ? AND color = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, pet);
            stmt.setString(3, food);
            stmt.setString(4, book);
            stmt.setString(5, color);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true jika semua jawaban cocok
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update password
    public void updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validateLogin(String username, String password) {
        return checkLogin(username, password);
    }

}
