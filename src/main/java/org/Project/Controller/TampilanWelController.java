package org.Project.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.Project.model.CatatanKeuangan;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class TampilanWelController {

    @FXML
    private TableView<CatatanKeuangan> tableRekap;
    @FXML
    private TableColumn<CatatanKeuangan, String> colJudul;
    @FXML
    private TableColumn<CatatanKeuangan, Double> colJumlah;
    @FXML
    private TableColumn<CatatanKeuangan, String> colKategori;
    @FXML
    private TableColumn<CatatanKeuangan, String> colTipe;
    @FXML
    private TableColumn<CatatanKeuangan, String> colTanggal;

    @FXML
    private Label lblWelcome;
    @FXML
    private TextField tfSearch;
    @FXML
    private DatePicker dpFilterMulai;
    @FXML
    private DatePicker dpFilterSelesai;
    @FXML
    private Label lblTotalPemasukan;
    @FXML
    private Label lblTotalPengeluaran;

    private ObservableList<CatatanKeuangan> dataKeuangan = FXCollections.observableArrayList();
    private int userId;

    // Dipanggil oleh controller lain setelah load FXML
    public void setUserId(int userId) {
        this.userId = userId;
        lblWelcome.setText("Selamat datang, User ID: " + userId);
        loadData();
    }

    @FXML
    private void initialize() {
        colJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colTipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        dpFilterMulai.setValue(null);
        dpFilterSelesai.setValue(null);
        dpFilterMulai.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterData();  // otomatis filter ulang setiap tanggal berubah
        });
        dpFilterSelesai.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterData();
        });

        // Listener pencarian otomatis
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData();
        });
    }


    @FXML
    private void loadData() {
        dataKeuangan.clear();
        String sql = "SELECT * FROM catatan_keuangan WHERE userId = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dataKeuangan.add(new CatatanKeuangan(
                        rs.getInt("id"),
                        rs.getInt("userId"),
                        rs.getString("judul"),
                        rs.getDouble("jumlah"),
                        rs.getString("kategori"),
                        rs.getString("tipe"),
                        rs.getString("tanggal")
                ));
            }
            tableRekap.setItems(dataKeuangan);
            updateTotals(); // update total pemasukan & pengeluaran setelah load data
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void nexDaftarCatatanKeuangan(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/catatan-keuangan-view.fxml"));
            Parent root = loader.load();

            // Kirim userId ke KeuanganController
            KeuanganController controller = loader.getController();
            controller.setUserId(userId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Catatan Keuangan");
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal memuat halaman Catatan Keuangan.");
        }
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/login-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            Stage currentStage = (Stage) lblWelcome.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka login page.");
        }
    }

    private void updateTotals() {
        double totalPemasukan = dataKeuangan.stream()
                .filter(c -> c.getTipe().equals("Pemasukan"))
                .mapToDouble(CatatanKeuangan::getJumlah)
                .sum();
        double totalPengeluaran = dataKeuangan.stream()
                .filter(c -> c.getTipe().equals("Pengeluaran"))
                .mapToDouble(CatatanKeuangan::getJumlah)
                .sum();
        lblTotalPemasukan.setText("Total Pemasukan: " + totalPemasukan);
        lblTotalPengeluaran.setText("Total Pengeluaran: " + totalPengeluaran);
    }


    @FXML
    private void filterData() {
        String searchQuery = tfSearch.getText().trim().toLowerCase();
        LocalDate filterMulai = dpFilterMulai.getValue();
        LocalDate filterSelesai = dpFilterSelesai.getValue();

        dataKeuangan.clear();

        // Jika search kosong dan tanggal kosong, tampilkan semua data tanpa filter
        if ((searchQuery.isEmpty()) && (filterMulai == null || filterSelesai == null)) {
            loadData();
            return;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM catatan_keuangan WHERE userId = ?");
        if (filterMulai != null && filterSelesai != null) {
            sqlBuilder.append(" AND tanggal BETWEEN ? AND ?");
        }
        if (!searchQuery.isEmpty()) {
            // Cari di judul, kategori, tipe, tanggal (bisa tambah kolom lain jika perlu)
            sqlBuilder.append(" AND (LOWER(judul) LIKE ? OR LOWER(kategori) LIKE ? OR LOWER(tipe) LIKE ? OR LOWER(tanggal) LIKE ?)");
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);

            if (filterMulai != null && filterSelesai != null) {
                stmt.setString(paramIndex++, filterMulai.toString());
                stmt.setString(paramIndex++, filterSelesai.toString());
            }

            if (!searchQuery.isEmpty()) {
                String likeQuery = "%" + searchQuery + "%";
                stmt.setString(paramIndex++, likeQuery);
                stmt.setString(paramIndex++, likeQuery);
                stmt.setString(paramIndex++, likeQuery);
                stmt.setString(paramIndex++, likeQuery);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dataKeuangan.add(new CatatanKeuangan(
                        rs.getInt("id"),
                        rs.getInt("userId"),
                        rs.getString("judul"),
                        rs.getDouble("jumlah"),
                        rs.getString("kategori"),
                        rs.getString("tipe"),
                        rs.getString("tanggal")
                ));
            }
            tableRekap.setItems(dataKeuangan);
            updateTotals();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}