package org.Project.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.Project.Database.CatatanDB;
import org.Project.model.CatatanKeuangan;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MengelolaCatatanController implements Initializable {

    private static final String DB_URL = "jdbc:sqlite:catatan.db";

    @FXML private TableView<CatatanKeuangan> tableKeuangan;
    @FXML private TableColumn<CatatanKeuangan, String> colJudul;
    @FXML private TableColumn<CatatanKeuangan, Double> colJumlah;
    @FXML private TableColumn<CatatanKeuangan, String> colKategori;
    @FXML private TableColumn<CatatanKeuangan, String> colTipe;
    @FXML private TableColumn<CatatanKeuangan, String> colTanggal;
    @FXML private TextField tfJudul;
    @FXML private TextField tfJumlah;
    @FXML private ComboBox<String> cbKategori;
    @FXML private ComboBox<String> cbTipe;
    @FXML private DatePicker dpTanggal;
    @FXML private DatePicker dpFilterMulai;
    @FXML private DatePicker dpFilterSelesai;
    @FXML private Label lblTotalPemasukan;
    @FXML private Label lblTotalPengeluaran;

    private ObservableList<CatatanKeuangan> dataKeuangan = FXCollections.observableArrayList();
    private int userId;

    private boolean editMode = false;
    private int idCatatanYangDiedit = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new CatatanDB().createTableIfNotExists();

        colJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        colJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colTipe.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        cbTipe.setItems(FXCollections.observableArrayList("income", "expense"));
        cbKategori.setItems(FXCollections.observableArrayList("salary", "food", "transport", "entertainment", "others"));

        dpTanggal.setValue(LocalDate.now());
        dpFilterMulai.setValue(null);
        dpFilterSelesai.setValue(null);


        dpFilterMulai.valueProperty().addListener((obs, oldVal, newVal) -> filterTanggal());
        dpFilterSelesai.valueProperty().addListener((obs, oldVal, newVal) -> filterTanggal());

        dpTanggal.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadAllData();
    }

    private void loadAllData() {
        dataKeuangan.clear();
        String query = "SELECT * FROM catatan_keuangan WHERE userId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

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
            showAlert("Database Error", e.getMessage());
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

        if (judul.isEmpty() || kategori == null || tipe == null || tanggal == null || jumlah <= 0) {
            showAlert("Form Incomplete", "Semua field wajib diisi dengan benar.");
            return;
        }

        if (editMode && idCatatanYangDiedit != -1) {
            // Mode edit: update data
            String sql = "UPDATE catatan_keuangan SET judul = ?, jumlah = ?, kategori = ?, tipe = ?, tanggal = ? WHERE id = ? AND userId = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, judul);
                stmt.setDouble(2, jumlah);
                stmt.setString(3, kategori);
                stmt.setString(4, tipe);
                stmt.setString(5, tanggal.toString());
                stmt.setInt(6, idCatatanYangDiedit);
                stmt.setInt(7, userId);

                stmt.executeUpdate();
                showAlert("Sukses", "Catatan berhasil diperbarui.");
                editMode = false;
                idCatatanYangDiedit = -1;
            } catch (SQLException e) {
                showAlert("Database Error", e.getMessage());
            }
        } else {
            // Mode tambah baru
            String sql = "INSERT INTO catatan_keuangan (userId, judul, jumlah, kategori, tipe, tanggal) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, userId);
                stmt.setString(2, judul);
                stmt.setDouble(3, jumlah);
                stmt.setString(4, kategori);
                stmt.setString(5, tipe);
                stmt.setString(6, tanggal.toString());

                stmt.executeUpdate();
                showAlert("Sukses", "Catatan berhasil ditambahkan.");
            } catch (SQLException e) {
                showAlert("Database Error", e.getMessage());
            }
        }

        loadAllData();
        clearForm();
    }

    @FXML
    private void hapusCatatan() {
        CatatanKeuangan selected = tableKeuangan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Pilih catatan yang ingin dihapus.");
            return;
        }

        String sql = "DELETE FROM catatan_keuangan WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();
            loadAllData();
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    @FXML
    private void filterTanggal() {
        LocalDate mulai = dpFilterMulai.getValue();
        LocalDate selesai = dpFilterSelesai.getValue();

        if (mulai == null || selesai == null) {
            loadAllData();
            return;
        }

        if (mulai.isAfter(selesai)) {
            showAlert("Invalid Filter", "Tanggal mulai harus sebelum tanggal selesai.");
            return;
        }

        dataKeuangan.clear();
        String query = "SELECT * FROM catatan_keuangan WHERE userId = ? AND tanggal BETWEEN ? AND ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

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
            showAlert("Filter Error", e.getMessage());
        }
    }

    private void updateTotals() {
        double totalIn = dataKeuangan.stream()
                .filter(d -> d.getTipe().equalsIgnoreCase("income"))
                .mapToDouble(CatatanKeuangan::getJumlah)
                .sum();

        double totalOut = dataKeuangan.stream()
                .filter(d -> d.getTipe().equalsIgnoreCase("expense"))
                .mapToDouble(CatatanKeuangan::getJumlah)
                .sum();

        lblTotalPemasukan.setText("Total Income: Rp. " + totalIn);
        lblTotalPengeluaran.setText("Total Expense: Rp. " + totalOut);
    }

    private void clearForm() {
        tfJudul.clear();
        tfJumlah.clear();
        cbKategori.getSelectionModel().clearSelection();
        cbTipe.getSelectionModel().clearSelection();
        dpTanggal.setValue(LocalDate.now());
        editMode = false;
        idCatatanYangDiedit = -1;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void kembaliKeWelcome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/tampilanHome-view.fxml"));
            Parent root = loader.load();

            TampilanHomeController controller = loader.getController();
            controller.setUserId(userId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.setMaximized(true);
        } catch (IOException e) {
            showAlert("Navigation Error", "Gagal kembali ke halaman utama.");
        }
    }

    private boolean bisaEdit(String tanggalCatatan) {
        LocalDate tanggal = LocalDate.parse(tanggalCatatan);
        return !LocalDate.now().isAfter(tanggal.plusMonths(1));
    }

    @FXML
    private void editCatatan() {
        CatatanKeuangan selected = tableKeuangan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Pilih catatan yang ingin diedit.");
            return;
        }

        if (!bisaEdit(selected.getTanggal())) {
            showAlert("Edit Blocked", "Catatan hanya bisa diedit dalam waktu 1 bulan.");
            return;
        }

        tfJudul.setText(selected.getJudul());
        tfJumlah.setText(String.valueOf(selected.getJumlah()));
        cbKategori.setValue(selected.getKategori());
        cbTipe.setValue(selected.getTipe());
        dpTanggal.setValue(LocalDate.parse(selected.getTanggal()));
        idCatatanYangDiedit = selected.getId();
        editMode = true;
    }

    @FXML
    private void resetFilter() {
        dpFilterMulai.setValue(null);
        dpFilterSelesai.setValue(null);
        loadAllData();
    }

}
