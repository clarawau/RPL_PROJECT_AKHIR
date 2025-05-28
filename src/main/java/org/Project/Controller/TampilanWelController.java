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

    private ObservableList<CatatanKeuangan> dataRekap = FXCollections.observableArrayList();
    private int userId;

    // Dipanggil oleh controller lain setelah load FXML
    public void setUserId(int userId) {
        this.userId = userId;
        lblWelcome.setText("Selamat datang, User ID: " + userId);
        loadData();
    }

    @FXML
    private void initialize() {
        // Konfigurasi kolom tabel
        colJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colTipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        dpFilterMulai.setValue(LocalDate.now().minusDays(7));
        dpFilterSelesai.setValue(LocalDate.now());
    }

    private void loadData() {
        dataRekap.clear();
        String sql = "SELECT * FROM catatan_keuangan WHERE userId = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dataRekap.add(new CatatanKeuangan(
                        rs.getInt("id"),
                        rs.getInt("userId"),
                        rs.getString("judul"),
                        rs.getDouble("jumlah"),
                        rs.getString("kategori"),
                        rs.getString("tipe"),
                        rs.getString("tanggal")
                ));
            }
            tableRekap.setItems(dataRekap);
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
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) lblWelcome.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka login page.");
        }
    }

    @FXML
    private void filterData() {
        String searchQuery = tfSearch.getText();
        LocalDate filterMulai = dpFilterMulai.getValue();
        LocalDate filterSelesai = dpFilterSelesai.getValue();

        dataRekap.clear();
        String sql = "SELECT * FROM catatan_keuangan WHERE userId = ? AND tanggal BETWEEN ? AND ? AND judul LIKE ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, filterMulai.toString());
            stmt.setString(3, filterSelesai.toString());
            stmt.setString(4, "%" + searchQuery + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dataRekap.add(new CatatanKeuangan(
                        rs.getInt("id"),
                        rs.getInt("userId"),
                        rs.getString("judul"),
                        rs.getDouble("jumlah"),
                        rs.getString("kategori"),
                        rs.getString("tipe"),
                        rs.getString("tanggal")
                ));
            }
            tableRekap.setItems(dataRekap);
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