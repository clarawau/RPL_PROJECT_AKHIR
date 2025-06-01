package org.Project.Controller;

import org.Project.Manager.LoginSession;
import org.Project.Manager.Session;
import org.Project.model.CatatanKeuangan;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class TampilanHomeController {
    @FXML private TableView<CatatanKeuangan> tableRekap;
    @FXML private TableColumn<CatatanKeuangan, String> colJudul;
    @FXML private TableColumn<CatatanKeuangan, Double> colJumlah;
    @FXML private TableColumn<CatatanKeuangan, String> colKategori;
    @FXML private TableColumn<CatatanKeuangan, String> colTipe;
    @FXML private TableColumn<CatatanKeuangan, String> colTanggal;

    @FXML private Label lblWelcome;
    @FXML private TextField tfSearch;
    @FXML private DatePicker dpFilterMulai;
    @FXML private DatePicker dpFilterSelesai;
    @FXML private Label lblTotalPemasukan;
    @FXML private Label lblTotalPengeluaran;
    @FXML private TextField tfBatasPengeluaran;
    @FXML private Button btnSetBatasPengeluaran;
    @FXML private Label lblPengeluaranBulanIni;
    @FXML private Label lblBatasPengeluaranAktif;
    @FXML private Region spacer1; // Diperlukan agar Region fx:id="spacer1" di FXML dikenali

    private ObservableList<CatatanKeuangan> dataKeuangan = FXCollections.observableArrayList();
    private int userId;
    private String username;
    private double batasPengeluaran = -1;
    private LocalDate tanggalSetBatas = null;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
        lblWelcome.setText("Welcome, " + username + "!");
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

        dpFilterMulai.valueProperty().addListener((obs, oldVal, newVal) -> filterData());
        dpFilterSelesai.valueProperty().addListener((obs, oldVal, newVal) -> filterData());
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> filterData());

        this.userId = Session.getInstance().getUserId();
        this.username = Session.getInstance().getUsername();
        lblWelcome.setText("Welcome, " + username + "!");
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
            tableRekap.setItems(dataKeuangan);
            updateTotals();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterData() {
        String searchQuery = tfSearch.getText().trim().toLowerCase();
        LocalDate mulai = dpFilterMulai.getValue();
        LocalDate selesai = dpFilterSelesai.getValue();

        if (mulai != null && selesai != null && mulai.isAfter(selesai)) {
            showAlert("Invalid Date", "Start date must be before or equal to end date.");
            return;
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM catatan_keuangan WHERE userId = ?");
        if (mulai != null && selesai != null) sql.append(" AND tanggal BETWEEN ? AND ?");
        if (!searchQuery.isEmpty())
            sql.append(" AND (LOWER(judul) LIKE ? OR LOWER(kategori) LIKE ? OR LOWER(tipe) LIKE ? OR LOWER(tanggal) LIKE ?)");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            stmt.setInt(idx++, userId);
            if (mulai != null && selesai != null) {
                stmt.setString(idx++, mulai.toString());
                stmt.setString(idx++, selesai.toString());
            }
            if (!searchQuery.isEmpty()) {
                String like = "%" + searchQuery + "%";
                for (int i = 0; i < 4; i++) stmt.setString(idx++, like);
            }

            dataKeuangan.clear();
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

    @FXML
    private void setBatasPengeluaran() {
        try {
            if (tanggalSetBatas != null) {
                LocalDate now = LocalDate.now();
                if (tanggalSetBatas.getMonth() == now.getMonth() && tanggalSetBatas.getYear() == now.getYear()) {
                    showAlert("Batas Sudah Ditentukan", "Batas pengeluaran sudah ditetapkan bulan ini.");
                    return;
                }
            }

            String input = tfBatasPengeluaran.getText().trim();
            if (input.isEmpty()) {
                showAlert("Input Required", "Please enter a spending limit.");
                return;
            }

            batasPengeluaran = Double.parseDouble(input);
            tanggalSetBatas = LocalDate.now();
            lblBatasPengeluaranAktif.setText("Limit: Rp. " + batasPengeluaran);
            showAlert("Limit Set", "Monthly spending limit set to Rp. " + batasPengeluaran);
            periksaPengeluaranBulanan(true);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number.");
        }
    }

    private void periksaPengeluaranBulanan(boolean tampilkanPeringatan) {
        double totalBulanIni = dataKeuangan.stream()
                .filter(k -> k.getTipe().equalsIgnoreCase("expense"))
                .filter(k -> {
                    try {
                        LocalDate tgl = LocalDate.parse(k.getTanggal());
                        LocalDate now = LocalDate.now();
                        return tgl.getMonth() == now.getMonth() && tgl.getYear() == now.getYear();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(CatatanKeuangan::getJumlah).sum();

        lblPengeluaranBulanIni.setText("This Month: Rp. " + totalBulanIni);

        if (batasPengeluaran > 0 && tanggalSetBatas != null) {
            LocalDate now = LocalDate.now();
            if (tanggalSetBatas.getMonth() == now.getMonth() && tanggalSetBatas.getYear() == now.getYear()) {
                lblBatasPengeluaranAktif.setText("Limit: Rp. " + batasPengeluaran);
                if (totalBulanIni > batasPengeluaran && tampilkanPeringatan) {
                    showAlert("Limit Exceeded", "Warning! Spending this month exceeds the limit of Rp. " + batasPengeluaran);
                }
            }
        }
    }

    private void updateTotals() {
        double pemasukan = dataKeuangan.stream()
                .filter(k -> k.getTipe().equalsIgnoreCase("income"))
                .mapToDouble(CatatanKeuangan::getJumlah).sum();

        double pengeluaran = dataKeuangan.stream()
                .filter(k -> k.getTipe().equalsIgnoreCase("expense"))
                .mapToDouble(CatatanKeuangan::getJumlah).sum();

        lblTotalPemasukan.setText("Total Income: Rp. " + pemasukan);
        lblTotalPengeluaran.setText("Total Expense: Rp. " + pengeluaran);

        periksaPengeluaranBulanan(true);
    }

    @FXML
    private void nexDaftarCatatanKeuangan(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/mengelolaCatatan-view.fxml"));
            Parent root = loader.load();

            MengelolaCatatanController controller = loader.getController();
            controller.setUserId(userId);
//

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Records");
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load record management page.");
        }
    }

    @FXML
    private void tampilkanGrafik() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/grafik-view.fxml"));
            Parent root = loader.load();

            GrafikController controller = loader.getController();
            controller.setUserId(userId);

            Stage stage = new Stage();
            stage.setTitle("Graphics");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();


            ((Stage) lblWelcome.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open graphic page.");
        }
    }


    @FXML
    private void logout() {
        LoginSession.saveSession(username);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            ((Stage) lblWelcome.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open login page.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
