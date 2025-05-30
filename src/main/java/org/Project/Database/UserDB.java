package org.Project.Database;

import java.sql.*;

public class UserDB {

    private static final String DB_URL = "jdbc:sqlite:catatan.db";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
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


    public UserDB() {

        createTableIfNotExists();


    }

    public boolean insertUser(String username, String password, String pet, String food, String book, String color) {
        String sql = "INSERT INTO users(username, password, pet, food, book, color) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase().trim());
            stmt.setString(2, password.trim());
            stmt.setString(3, pet.toLowerCase().trim());
            stmt.setString(4, food.toLowerCase().trim());
            stmt.setString(5, book.toLowerCase().trim());
            stmt.setString(6, color.toLowerCase().trim());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cek apakah username sudah ada
    public boolean isUsernameExist(String username) {
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase().trim());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cek login username dan password
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT id FROM users WHERE LOWER(username) = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase().trim());
            stmt.setString(2, password.trim());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Verifikasi jawaban keamanan untuk reset password
    public boolean verifySecurityAnswers(String username, String pet, String food, String book, String color) {
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = ? AND LOWER(pet) = ? AND LOWER(food) = ? AND LOWER(book) = ? AND LOWER(color) = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase().trim());
            stmt.setString(2, pet.toLowerCase().trim());
            stmt.setString(3, food.toLowerCase().trim());
            stmt.setString(4, book.toLowerCase().trim());
            stmt.setString(5, color.toLowerCase().trim());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update password user
    public void updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE LOWER(username) = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword.trim());
            stmt.setString(2, username.toLowerCase().trim());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Reset password dengan verifikasi jawaban keamanan
    public boolean resetPassword(String username, String pet, String food, String book, String color, String newPassword) {
        if (verifySecurityAnswers(username, pet, food, book, color)) {
            updatePassword(username, newPassword);
            return true;
        } else {
            return false;
        }
    }

    // Mendapatkan user ID berdasarkan username
    public int getUserId(String username) {
        String sql = "SELECT id FROM users WHERE LOWER(username) = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase().trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Validasi login (alias dari checkLogin)
    public boolean validateLogin(String username, String password) {
        return checkLogin(username, password);
    }
}
