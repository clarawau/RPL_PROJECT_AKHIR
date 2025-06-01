package org.Project.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
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
        String sql = "SELECT kategori, tipe, SUM(jumlah) as total " +
                "FROM catatan_keuangan WHERE userId = ? " +
                "GROUP BY kategori, tipe";

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expense");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:catatan.db");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String kategori = rs.getString("kategori");
                String tipe = rs.getString("tipe");
                double jumlah = rs.getDouble("total");

                if (tipe.equalsIgnoreCase("income")) {
                    incomeSeries.getData().add(new XYChart.Data<>(kategori, jumlah));
                } else if (tipe.equalsIgnoreCase("expense")) {
                    expenseSeries.getData().add(new XYChart.Data<>(kategori, jumlah));
                }
            }

            barChart.getData().clear();
            barChart.getData().addAll(incomeSeries, expenseSeries);

            Platform.runLater(() -> {
                colorBarSeries(incomeSeries, "#4CAF50"); // Hijau
                colorBarSeries(expenseSeries, "#F44336"); // Merah
                addDataLabel(incomeSeries);
                addDataLabel(expenseSeries);
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void colorBarSeries(XYChart.Series<String, Number> series, String color) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
            } else {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-bar-fill: " + color + ";");
                    }
                });
            }
        }
    }

    private void addDataLabel(XYChart.Series<String, Number> series) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            if (node != null) {
                Label label = new Label(String.format("%,.0f", data.getYValue().doubleValue()));
                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

                node.parentProperty().addListener((obs, oldParent, newParent) -> {
                    if (newParent instanceof Group) {
                        ((Group) newParent).getChildren().add(label);
                    }
                });

                node.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
                    label.setLayoutX(newBounds.getMinX() + newBounds.getWidth() / 2 - label.prefWidth(-1) / 2);
                    label.setLayoutY(newBounds.getMinY() - label.prefHeight(-1) - 5);
                });
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
}
