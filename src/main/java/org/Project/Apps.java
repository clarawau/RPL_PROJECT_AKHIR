package org.Project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.Project.Database.UserDB;

import java.io.IOException;

public class Apps extends Application {

    private static Stage primaryStage; // simpan referensi stage utama agar bisa diakses statis

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Inisialisasi database users, akan membuat tabel jika belum ada
        new UserDB();

        // Load tampilan pertama aplikasi (Registrasi, atau bisa kamu ubah ke Login jika mau)
        Parent root = FXMLLoader.load(getClass().getResource("/org/Project/TampilanWel-view.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Login Page");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Method statis untuk mengganti root scene
    public static void setRoot(String fxml, String title, boolean isResizable) throws IOException {
        Parent root = FXMLLoader.load(Apps.class.getResource("/org/Project/" + fxml + ".fxml"));
        primaryStage.getScene().setRoot(root);
        primaryStage.sizeToScene();
        primaryStage.setResizable(isResizable);
        if (title != null) {
            primaryStage.setTitle(title);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
