package org.Project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Apps extends Application {

    public static void setRoot(String tampilanAwal, String daftarCatatan, boolean b) {
    }

    public static void openViewWithModal(String s, boolean b) {
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Cek atau buat tabel 'users'
        JdbcDao dao = new JdbcDao();
        dao.createUsersTableIfNotExists();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Aplikasi Catatan");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
