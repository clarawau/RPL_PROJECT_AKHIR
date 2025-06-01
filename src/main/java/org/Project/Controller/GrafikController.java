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
        double totalIncome = 0;
        double totalExpense = 0;

        String sql = "SELECT tipe, SUM(jumlah) as total FROM catatan_keuangan WHERE userId = ? GROUP BY tipe";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String tipe = rs.getString("tipe");
                double jumlah = rs.getDouble("total");

                if (tipe.equalsIgnoreCase("income")) {
                    totalIncome = jumlah;
                } else if (tipe.equalsIgnoreCase("expense")) {
                    totalExpense = jumlah;
                }
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Comparison");

            series.getData().add(new XYChart.Data<>("Income", totalIncome));
            series.getData().add(new XYChart.Data<>("Expense", totalExpense));

            barChart.getData().clear();
            barChart.getData().add(series);

            Platform.runLater(() -> {
                setSingleBarColor(series, "#4CAF50", 0); // income: hijau
                setSingleBarColor(series, "#F44336", 1); // expense: merah
                fixLegendColor(); // optional kalau pakai legend custom
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setSingleBarColor(XYChart.Series<String, Number> series, String color, int index) {
        if (series.getData().size() > index) {
            Node node = series.getData().get(index).getNode();
            if (node != null) {
                node.setStyle("-fx-bar-fill: " + color + ";");
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/tampilanHome-view.fxml"));
            Parent root = loader.load();

            TampilanHomeController controller = loader.getController();
            controller.setUserId(userId);

            Stage stage = (Stage) barChart.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("Failed to return to homepage.");
            alert.showAndWait();
        }
    }

    private void fixLegendColor() {
        for (Node node : barChart.lookupAll(".chart-legend-item")) {
            if (node instanceof Label label) {
                String text = label.getText();
                Node symbol = label.getGraphic();
                if (symbol != null) {
                    if ("Income".equalsIgnoreCase(text)) {
                        symbol.setStyle("-fx-background-color: #4CAF50;");
                    } else if ("Expense".equalsIgnoreCase(text)) {
                        symbol.setStyle("-fx-background-color: #F44336;");
                    }
                }
            }
        }
    }
}
