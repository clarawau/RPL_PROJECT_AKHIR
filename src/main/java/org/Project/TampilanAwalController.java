package org.Project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TampilanAwalController implements Initializable {

    @FXML private TableView<Catatan2> table;
    @FXML private TableColumn<Catatan2, Integer> id;
    @FXML private TableColumn<Catatan2, String> judul;
    @FXML private TableColumn<Catatan2, String> kategori;
    @FXML private TextField searchBox;

    private final ObservableList<Catatan2> catatanObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        judul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));

        getAllData();
    }

    private void getAllData() {
        catatanObservableList.clear();
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:catatanku.db");
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM catatan")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String judul = rs.getString("judul");
                String kategori = rs.getString("kategori");
                catatanObservableList.add(new Catatan2(id, judul, kategori));
            }

            table.setItems(catatanObservableList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Gagal mengambil data dari database:\n" + e.getMessage());
            Platform.exit(); // keluar dari aplikasi
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Tombol handler dari FXML
    @FXML
    private void onBtnkpClick() throws IOException {
        // Pindah ke halaman daftar-catatan-view
        Apps.setRoot("daftar-catatan-view", "Daftar Catatan", false);

        // Tutup jendela saat ini (halaman utama)
        Stage currentStage = (Stage) table.getScene().getWindow();
        currentStage.close();
    }


    @FXML
    private void onBtnGrafikClick() {
        // TODO
    }

    @FXML
    private void onBtnlogoutClick() throws IOException {
        // Load login-view.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Parent root = loader.load();

        // Buat stage (jendela) baru
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));
        loginStage.show();

        // Tutup jendela saat ini (TampilanAwal)
        Stage currentStage = (Stage) table.getScene().getWindow();
        currentStage.close();
    }
}
