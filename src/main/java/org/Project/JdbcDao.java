package org.Project;

import java.sql.*;

public class JdbcDao {

    private final String jdbcURL = "jdbc:sqlite:database.db";


    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL);
    }

    public void createUsersTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "nickname TEXT, " +
                "favorite_color TEXT, " +
                "birth_date TEXT)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabel users siap digunakan.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void insertRecord(String fullName, String email, String password, String nickname, String favoriteColor, String birthDate) {
        String sql = "INSERT INTO users(full_name, email, password, nickname, favorite_color, birth_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, nickname);
            preparedStatement.setString(5, favoriteColor);
            preparedStatement.setString(6, birthDate);
            preparedStatement.executeUpdate();

            System.out.println("User berhasil diregistrasi: " + email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validateLogin(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
