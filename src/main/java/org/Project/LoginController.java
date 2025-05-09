package org.Project;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

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
        jdbcDao.createUsersTableIfNotExists();
        boolean validUser = jdbcDao.validateLogin(email, password);

        Alert alert;
        if (validUser) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sukses");
            alert.setContentText("Login berhasil!");
            alert.showAndWait();

            // Tutup jendela login
            Stage currentStage = (Stage) txtUsername.getScene().getWindow();
            currentStage.close();

            // Buka tampilan utama
            Apps.setRoot("TampilanAwal", "Daftar Catatan", false);
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Gagal");
            alert.setContentText("Login gagal! Email atau password salah.");
            alert.showAndWait();
            txtUsername.requestFocus();
        }
    }

}
