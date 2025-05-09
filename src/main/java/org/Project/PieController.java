package org.Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

import java.awt.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PieController implements Initializable {
    @FXML
    private TextField txtSearch;


    @FXML
    private PieChart pieChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preparedData();
    }

    private void preparedData() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        String query = "SELECT kategori, COUNT(*) AS jumlah " +
                "FROM catatan " +
                "WHERE kategori IN ('Belanja', 'Khusus', 'Percintaan', 'Self Development') " +
                "GROUP BY kategori";


        try (PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String kategori = resultSet.getString("kategori");
                int jumlah = resultSet.getInt("jumlah");
                pieChartData.add(new PieChart.Data(kategori, jumlah));
            }

            pieChart.setData(pieChartData);
            pieChart.setTitle("Jumlah Catatan berdasarkan Kategori");
            pieChart.setClockwise(true);
            pieChart.setLabelLineLength(50);
            pieChart.setLabelsVisible(true);
            pieChart.setStartAngle(180);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
