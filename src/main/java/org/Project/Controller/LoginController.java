package org.Project.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.Project.Apps;
import org.Project.DataBase.JdbcDao;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    @FXML
    protected void onKeyPressEvent(KeyEvent event) throws IOException, SQLException {
        if (event.getCode() == KeyCode.ENTER) {
            btnLoginClick();
        }
    }

    @FXML
    protected void btnLoginClick() throws IOException, SQLException {
        String email = txtUsername.getText();
        String password = txtPassword.getText();

        JdbcDao jdbcDao = new JdbcDao();
        boolean validUser = jdbcDao.loginUser(email, password);
        Alert alert;
        if (validUser) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sukses");
            alert.setContentText("Login berhasil!");
            alert.showAndWait();

            Stage currentStage = (Stage) txtUsername.getScene().getWindow();
            currentStage.close();

// Langsung buka TampilanAwal.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/TampilanAwal.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());
            stage.setTitle("Daftar Catatan");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Gagal");
            alert.setContentText("Login gagal! Email atau password salah.");
            alert.showAndWait();
            txtUsername.requestFocus();
        }
    }
}
