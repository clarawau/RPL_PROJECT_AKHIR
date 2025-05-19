package org.Project.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.Project.Database.DBConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class GrafikController implements Initializable {

    @FXML
    private LineChart<String, Number> lineChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showLineChart();
    }

    private void showLineChart() {
        String query = "SELECT kategori, COUNT(*) AS jumlah " +
                "FROM catatan " +
                "GROUP BY kategori";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Jumlah Catatan");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String kategori = rs.getString("kategori");
                int jumlah = rs.getInt("jumlah");
                series.getData().add(new XYChart.Data<>(kategori, jumlah));
            }

            lineChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
