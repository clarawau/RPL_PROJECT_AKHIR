package org.Project.Controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.Project.Apps;
import org.Project.model.Catatan;
import org.Project.model.Catatan2;

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

    // diasumsikan ada di FXML lain atau akan ditambahkan nanti
    @FXML private TextField txtFldJudul;
    @FXML private TextArea txtAreaKonten;
    @FXML private ComboBox<String> cbKategori;

    private ObservableList<Catatan2> catatanObservableList = FXCollections.observableArrayList();
    private Catatan selectedCatatan;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        catatanObservableList = FXCollections.observableArrayList();
        table.setItems(catatanObservableList);

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        judul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));

        getConnection();
        createTable();
        getAllData();

        FilteredList<Catatan2> filteredList = new FilteredList<>(catatanObservableList, b -> true);
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(catatan -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return catatan.getJudul().toLowerCase().contains(lowerCaseFilter)
                        || catatan.getKategori().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Catatan2> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);

        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Catatan2>() {
            @Override
            public void changed(ObservableValue<? extends Catatan2> observableValue, Catatan2 course, Catatan2 t1) {
                if (observableValue.getValue() != null) {
                    selectedCatatan = new Catatan(); // karena selectedCatatan bertipe Catatan
                    selectedCatatan.setJudul(observableValue.getValue().getJudul());
                    selectedCatatan.setKategori(observableValue.getValue().getKategori());
                    selectedCatatan.setKonten(""); // karena Catatan2 tidak punya konten

                    txtFldJudul.setText(observableValue.getValue().getJudul());
                    txtAreaKonten.setText(""); // Catatan2 tidak punya konten
                    cbKategori.setValue(observableValue.getValue().getKategori());
                }
            }
        });

        cbKategori.getItems().add(Catatan.CATATAN_SELF_DEVELOPEMENT);
        cbKategori.getItems().add(Catatan.CATATAN_BELANJA);
        cbKategori.getItems().add(Catatan.CATATAN_KHUSUS);
        cbKategori.getItems().add(Catatan.CATATAN_PERCINTAAN);
        bersihkan();
    }

    private void bersihkan() {
        if (txtFldJudul != null) txtFldJudul.clear();
        if (txtAreaKonten != null) txtAreaKonten.clear();
        if (cbKategori != null) cbKategori.getSelectionModel().clearSelection();
    }

    private void getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.getConnection("jdbc:sqlite:catatanku.db");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Koneksi Gagal", "Tidak dapat terhubung ke database.");
        }
    }

    private void createTable() {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:catatanku.db");
             Statement st = con.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS catatan ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "judul TEXT NOT NULL,"
                    + "kategori TEXT NOT NULL)";
            st.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Gagal membuat tabel:\n" + e.getMessage());
        }
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
            Platform.exit();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onBtnkpClick() throws IOException {
<<<<<<< Updated upstream
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/views/daftar-catatan-view.fxml"));
        Parent root = loader.load();


        Stage loginStage = new Stage();
        loginStage.setTitle("daftar catatan");
        loginStage.setScene(new Scene(root));
        loginStage.show();


        Stage currentStage = (Stage) table.getScene().getWindow();
        currentStage.close();
=======
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/Project/daftar-catatan-view.fxml"));
        Stage dfStage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        dfStage.setTitle("daftarcatatan");
        dfStage.setScene(scene);
        dfStage.setResizable(false);
        dfStage.show();
>>>>>>> Stashed changes
    }

    @FXML
    private void onBtnGrafikClick() {
        // TODO
    }

    @FXML
    private void onBtnlogoutClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Parent root = loader.load();

        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));
        loginStage.show();

        Stage currentStage = (Stage) table.getScene().getWindow();
        currentStage.close();
    }

}
