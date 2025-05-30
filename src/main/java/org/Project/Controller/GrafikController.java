package org.Project.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class GrafikController {

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
        loadBarChartData();
    }

    private void loadBarChartData() {
        HashMap<String, Double> pemasukanMap = new HashMap<>();
        HashMap<String, Double> pengeluaranMap = new HashMap<>();

        String sql = "SELECT kategori, tipe, SUM(jumlah) as total FROM catatan_keuangan WHERE userId = ? GROUP BY kategori, tipe";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String kategori = rs.getString("kategori");
                String tipe = rs.getString("tipe");
                double jumlah = rs.getDouble("total");

                if (tipe.equalsIgnoreCase("Pemasukan")) {
                    pemasukanMap.put(kategori, jumlah);
                } else if (tipe.equalsIgnoreCase("Pengeluaran")) {
                    pengeluaranMap.put(kategori, jumlah);
                }
            }

            XYChart.Series<String, Number> pemasukanSeries = new XYChart.Series<>();
            pemasukanSeries.setName("Pemasukan");

            XYChart.Series<String, Number> pengeluaranSeries = new XYChart.Series<>();
            pengeluaranSeries.setName("Pengeluaran");

            for (String kategori : pemasukanMap.keySet()) {
                pemasukanSeries.getData().add(new XYChart.Data<>(kategori, pemasukanMap.get(kategori)));
            }

            for (String kategori : pengeluaranMap.keySet()) {
                pengeluaranSeries.getData().add(new XYChart.Data<>(kategori, pengeluaranMap.get(kategori)));
            }

            barChart.getData().clear();
            barChart.getData().addAll(pemasukanSeries, pengeluaranSeries);

            // Ubah warna batang & legend setelah chart ditampilkan
            Platform.runLater(() -> {
                setSeriesColor(pemasukanSeries, "#4CAF50");   // Hijau
                setSeriesColor(pengeluaranSeries, "#F44336"); // Merah
                fixLegendColor(); // Perbaiki warna legend
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Set warna batang berdasarkan warna HEX
    private void setSeriesColor(XYChart.Series<String, Number> series, String color) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.setStyle("-fx-bar-fill: " + color + ";");
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/tampilanwel-view.fxml"));
            Parent root = loader.load();

            TampilanWelController controller = loader.getController();
            controller.setUserId(userId); // Kirim userId ke tampilanWel agar bisa load data user ini

            Stage stage = (Stage) barChart.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Optional: alert error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal kembali ke Tampilan Wel.");
            alert.showAndWait();
        }
    }


    // Perbaiki warna legend sesuai nama series
    private void fixLegendColor() {
        for (Node node : barChart.lookupAll(".chart-legend-item")) {
            if (node instanceof Label label) {
                String text = label.getText();
                Node symbol = label.getGraphic();
                if (symbol != null) {
                    if ("Pemasukan".equalsIgnoreCase(text)) {
                        symbol.setStyle("-fx-background-color: #4CAF50;");
                    } else if ("Pengeluaran".equalsIgnoreCase(text)) {
                        symbol.setStyle("-fx-background-color: #F44336;");
                    }
                }
            }
        }
    }
}
