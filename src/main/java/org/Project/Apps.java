package org.Project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.Project.DataBase.JdbcDao;

public class Apps extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Inisialisasi dan buat tabel user jika belum ada
        JdbcDao dao = new JdbcDao();
        dao.createTableIfNotExists();

<<<<<<< Updated upstream
        // Load halaman login
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/Project/Registrasi-view.fxml"));
=======
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TampilanAwal.fxml"));
>>>>>>> Stashed changes
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Aplikasi Catatan");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Optional: Placeholder jika ingin dipakai nanti
    public static void setRoot(String view1, String view2, boolean modal) {
        // Implementasi ganti scene kalau dibutuhkan
    }

    public static void openViewWithModal(String fxmlPath, boolean modal) {
        // Implementasi buka modal window kalau dibutuhkan
    }
}
