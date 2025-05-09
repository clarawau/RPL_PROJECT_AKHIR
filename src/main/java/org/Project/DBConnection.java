package org.Project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Ganti path sesuai dengan lokasi file database Anda
    private static final String DB_URL = "jdbc:sqlite:C:/path/to/your/db/catatanku.db";  // Pastikan path ini benar
    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) { // Cek jika koneksi belum ada atau sudah ditutup
                // Membuka koneksi SQLite
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("Gagal membuka koneksi ke database.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close(); // Menutup koneksi database
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi ke database.");
                e.printStackTrace();
            }
        }
    }
}
