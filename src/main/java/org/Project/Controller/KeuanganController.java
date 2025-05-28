package org.Project.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.Project.model.CatatanKeuangan;
import org.Project.Database.CatatanDB;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class KeuanganController implements Initializable {

    @FXML
    private TableView<CatatanKeuangan> tableKeuangan;
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
    private TextField tfJudul;
    @FXML
    private TextField tfJumlah;
    @FXML
    private ComboBox<String> cbKategori;
    @FXML
    private ComboBox<String> cbTipe;
    @FXML
    private DatePicker dpTanggal;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Buat tabel jika belum ada
        CatatanDB db = new CatatanDB();
        db.createTableIfNotExists();

        // Inisialisasi kolom tabel
        colJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colTipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        // Inisialisasi combo box
        cbTipe.setItems(FXCollections.observableArrayList("Pemasukan", "Pengeluaran"));
        cbKategori.setItems(FXCollections.observableArrayList("Gaji", "Makan", "Transportasi", "Hiburan", "Lainnya"));

        dpTanggal.setValue(LocalDate.now()); // Set default tanggal

        // Jangan load data di sini karena userId belum tersedia
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadData();
    }

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
            tableKeuangan.setItems(dataKeuangan);
            updateTotals();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void tambahCatatan() {
        String judul = tfJudul.getText();
        double jumlah;

        try {
            jumlah = Double.parseDouble(tfJumlah.getText());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Jumlah harus berupa angka.");
            return;
        }

        String kategori = cbKategori.getValue();
        String tipe = cbTipe.getValue();
        LocalDate tanggal = dpTanggal.getValue();

        if (judul.isEmpty() || jumlah <= 0 || kategori == null || tipe == null || tanggal == null) {
            showAlert("Input Error", "Semua field harus diisi dengan benar.");
            return;
        }

        String sql = "INSERT INTO catatan_keuangan (userId, judul, jumlah, kategori, tipe, tanggal) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, judul);
            stmt.setDouble(3, jumlah);
            stmt.setString(4, kategori);
            stmt.setString(5, tipe);
            stmt.setString(6, tanggal.toString());
            stmt.executeUpdate();
            loadData();
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void hapusCatatan() {
        CatatanKeuangan selected = tableKeuangan.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String sql = "DELETE FROM catatan_keuangan WHERE id = ?";
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Hapus Gagal", "Pilih catatan yang ingin dihapus.");
        }
    }

    @FXML
    private void filterTanggal() {
        LocalDate mulai = dpFilterMulai.getValue();
        LocalDate selesai = dpFilterSelesai.getValue();

        if (mulai == null || selesai == null) {
            showAlert("Filter Error", "Silakan pilih tanggal mulai dan selesai.");
            return;
        }

        dataKeuangan.clear();
        String sql = "SELECT * FROM catatan_keuangan WHERE userId = ? AND tanggal BETWEEN ? AND ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, mulai.toString());
            stmt.setString(3, selesai.toString());
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
            tableKeuangan.setItems(dataKeuangan);
            updateTotals();
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void clearForm() {
        tfJudul.clear();
        tfJumlah.clear();
        cbKategori.getSelectionModel().clearSelection();
        cbTipe.getSelectionModel().clearSelection();
        dpTanggal.setValue(LocalDate.now());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void kembaliKeWelcome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/TampilanWel-view.fxml"));
            Parent root = loader.load();
            TampilanWelController controller = loader.getController();
            controller.setUserId(userId);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tampilan Awal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke halaman utama.");
        }
    }
}
