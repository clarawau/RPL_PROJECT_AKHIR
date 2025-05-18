package org.Project.DataBase;

import java.sql.*;

public class JdbcDao {

    private static final String DB_URL = "jdbc:sqlite:users.db";

    // Buat koneksi ke database SQLite
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Buat tabel jika belum ada
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
            stmt.setString(1, username.toLowerCase());
            stmt.setString(2, password);
            stmt.setString(3, pet.toLowerCase());
            stmt.setString(4, food.toLowerCase());
            stmt.setString(5, book.toLowerCase());
            stmt.setString(6, color.toLowerCase());
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
            stmt.setString(1, username.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase());
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verifySecurityAnswers(String username, String pet, String food, String book, String color) {
        String sql = "SELECT 1 FROM users WHERE " +
                "LOWER(username) = ? AND LOWER(pet) = ? AND LOWER(food) = ? AND LOWER(book) = ? AND LOWER(color) = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.toLowerCase());
            stmt.setString(2, pet.toLowerCase());
            stmt.setString(3, food.toLowerCase());
            stmt.setString(4, book.toLowerCase());
            stmt.setString(5, color.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE LOWER(username) = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username.toLowerCase());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validateLogin(String username, String password) {
        return checkLogin(username, password);
    }
}
