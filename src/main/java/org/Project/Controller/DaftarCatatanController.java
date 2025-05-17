package org.Project.Controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.Project.Apps;
import org.Project.model.Catatan;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.function.Predicate;



public class DaftarCatatanController implements Initializable {
    @FXML
    private Button btnbck;
    @FXML
    private TextField txtFldJudul;
    @FXML
    private TextArea txtAreaKonten;
    private ObservableList<Catatan> catatanObservableList;
    @FXML
    private TableView<Catatan> table;
    @FXML
    private TableColumn<Catatan, String> id;
    @FXML
    private TableColumn<Catatan, String> judul;
    @FXML
    private TableColumn<Catatan, String> kategori;
    @FXML
    private TextField searchBox;
    @FXML
    private Button btnbr;

    @FXML
    private void onBtnkpClick(ActionEvent event) {

    }
    @FXML
    private ChoiceBox<String> cbKategori;
    private final String DB_URL = "jdbc:sqlite:catatanku.db";
    private Connection connection;
    Catatan selectedCatatan;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        catatanObservableList = FXCollections.observableArrayList();
        table.setItems(catatanObservableList);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        judul.setCellValueFactory(new PropertyValueFactory<>("judul"));
        kategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        getConnection();
        createTable();
        getAllData();
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Catatan>() {
            @Override
            public void changed(ObservableValue<? extends Catatan> observableValue, Catatan course, Catatan t1) {
                if (observableValue.getValue() != null) {
                    selectedCatatan = observableValue.getValue();
                    txtFldJudul.setText(observableValue.getValue().getJudul());
                    txtAreaKonten.setText(observableValue.getValue().getKonten());
                    cbKategori.setValue(observableValue.getValue().getKategori());
                }
            }
        });

        searchBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            String searchText = searchBox.getText();
            getAllDataFiltered(searchText);
        });

        //setup combo box
        cbKategori.getItems().add(Catatan.CATATAN_SELF_DEVELOPEMENT);
        cbKategori.getItems().add(Catatan.CATATAN_BELANJA);
        cbKategori.getItems().add(Catatan.CATATAN_KHUSUS);
        cbKategori.getItems().add(Catatan.CATATAN_PERCINTAAN);
        bersihkan();

    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL);
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
    }


    public void createTable() {
        String mhsTableSql = "CREATE TABLE IF NOT EXISTS catatan ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "judul TEXT NOT NULL,"
                + "konten TEXT NOT NULL,"
                + "kategori TEXT NOT NULL"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(mhsTableSql);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private Predicate<Catatan> createPredicate(String searchText) {
        return catatan -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return searchFindsCatatan(catatan, searchText);
        };
    }

    private boolean searchFindsCatatan(Catatan catatan, String searchText) {
        return (catatan.getJudul().toLowerCase().contains(searchText.toLowerCase())) ||
                (catatan.getKonten().toLowerCase().contains(searchText.toLowerCase())) ||
                (catatan.getKategori().toLowerCase().contains(searchText.toLowerCase()));
    }

    private void getAllData() {
        String query = "SELECT * FROM catatan";
        catatanObservableList.clear();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String judul = resultSet.getString("judul");
                String konten = resultSet.getString("konten");
                String kategori = resultSet.getString("kategori");
                Catatan catatan = new Catatan(id, judul, konten, kategori);
                catatanObservableList.addAll(catatan);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private void getAllDataFiltered(String word) {
        String query = "SELECT * FROM catatan WHERE judul like '%" + word +"%' OR kategori like '%" + word +"%'";
        catatanObservableList.clear();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String judul = resultSet.getString("judul");
                String konten = resultSet.getString("konten");
                String kategori = resultSet.getString("kategori");
                Catatan catatan = new Catatan(id, judul, konten, kategori);
                catatanObservableList.addAll(catatan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database query error
        }
    }

    private void bersihkan() {
        txtFldJudul.clear();
        txtAreaKonten.clear();
        txtFldJudul.requestFocus();
        table.getSelectionModel().clearSelection();
        selectedCatatan = null;
        cbKategori.getSelectionModel().clearSelection();
        cbKategori.getSelectionModel().select(0);
    }

    private boolean isCatatanUpdated() {
        if (selectedCatatan == null) {
            return false;
        }
        if (!selectedCatatan.getJudul().equalsIgnoreCase(txtFldJudul.getText()) ||
                !selectedCatatan.getKonten().equalsIgnoreCase(txtAreaKonten.getText()) ||
                !selectedCatatan.getKategori().equalsIgnoreCase(cbKategori.getSelectionModel().getSelectedItem())) {
            return true;
        } else {
            return false;
        }
    }

    @FXML
    protected void onBtnSimpanClick() {
        if (isCatatanUpdated()) {
            if (updateCatatan(selectedCatatan, new Catatan(selectedCatatan.getId(), txtFldJudul.getText(), txtAreaKonten.getText(), cbKategori.getSelectionModel().getSelectedItem()))) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Catatan Dirubah!");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Catatan gagal Dirubah!");
                alert.show();
            }
        } else {
            if (addCatatan(new Catatan(txtFldJudul.getText(), txtAreaKonten.getText(), cbKategori.getSelectionModel().getSelectedItem()))) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Catatan Ditambahkan!");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Catatan gagal Ditambahkan!");
                alert.show();
            }
        }
        bersihkan();
    }
    @FXML
    protected void onBtnHapus() {
        if (selectedCatatan != null && deleteCatatan(selectedCatatan)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Catatan Dihapus!");
            alert.show();
            bersihkan();
        }
    }

    public boolean deleteCatatan(Catatan catatan) {
        String query = "DELETE FROM catatan WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, catatan.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                catatanObservableList.remove(catatan);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean addCatatan(Catatan catatan) {
        String queryGetNextId = "SELECT seq FROM SQLITE_SEQUENCE WHERE name = 'catatan' LIMIT 1";
        String queryInsert = "INSERT INTO catatan (judul, konten, kategori) VALUES (?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement getNextIdStatement = connection.prepareStatement(queryGetNextId);
                 PreparedStatement insertStatement = connection.prepareStatement(queryInsert)) {
                ResultSet resultSet = getNextIdStatement.executeQuery();
                int nextId = 1;
                if (resultSet.next()) {
                    nextId = resultSet.getInt("seq") + 1;
                }
                insertStatement.setString(1, catatan.getJudul());
                insertStatement.setString(2, catatan.getKonten());
                insertStatement.setString(3, catatan.getKategori());
                int rowsAffected = insertStatement.executeUpdate();
                if (rowsAffected > 0) {
                    catatan.setId(nextId);
                    catatanObservableList.add(catatan);
                    connection.commit();
                    return true;
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean updateCatatan(Catatan oldCatatan, Catatan newCatatan) {
        String query = "UPDATE catatan SET judul = ?, konten = ?,  kategori = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newCatatan.getJudul());
            preparedStatement.setString(2, newCatatan.getKonten());
            preparedStatement.setString(3, newCatatan.getKategori());
            preparedStatement.setInt(4, oldCatatan.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                int iOldCatatan = catatanObservableList.indexOf(oldCatatan);
                catatanObservableList.set(iOldCatatan, newCatatan);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }

    @FXML
    protected void onBtnCloseClick() {
        Platform.exit();
    }

    @FXML
    public void onBtnGrafikClick() throws IOException {
        Apps.openViewWithModal("Grafik-view", false);
    }

    private boolean searchFindsMahasiswa(Catatan catatan, String searchText) {
        return (catatan.getJudul().toLowerCase().contains(searchText.toLowerCase())) ||
                (catatan.getKategori().toLowerCase().contains(searchText.toLowerCase()));
    }


    public void onbtnbrclick(ActionEvent actionEvent) {


    }

    public void onBtnlogoutClick(ActionEvent actionEvent) {
    }

    @FXML
    void onclickbtbnbck(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TampilanAwal.fxml"));
        Parent root = loader.load();


        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(root));
        loginStage.show();


        Stage currentStage = (Stage) table.getScene().getWindow();
        currentStage.close();


    }

}