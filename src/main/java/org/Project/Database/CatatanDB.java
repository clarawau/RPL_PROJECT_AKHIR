package org.Project.Database;

import org.Project.model.CatatanKeuangan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class CatatanDB {


    public static Connection getConnection() {
        try {

            return DriverManager.getConnection("jdbc:sqlite:catatan.db");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createTableIfNotExists() {
        String sql = """
        CREATE TABLE IF NOT EXISTS catatan_keuangan (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            userId INTEGER NOT NULL,
            judul TEXT NOT NULL,
            jumlah REAL NOT NULL,
            kategori TEXT NOT NULL,
            tipe TEXT NOT NULL,
            tanggal TEXT NOT NULL
        );
    """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean insertCatatan(CatatanKeuangan catatan) {
        String sql = "INSERT INTO catatan_keuangan(userId, judul, jumlah, kategori, tipe, tanggal) VALUES ( ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, catatan.getUserId());
            stmt.setString(2, catatan.getJudul());
            stmt.setDouble(3, catatan.getJumlah());
            stmt.setString(4, catatan.getKategori());
            stmt.setString(5, catatan.getTipe());
            stmt.setString(6, catatan.getTanggal());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
