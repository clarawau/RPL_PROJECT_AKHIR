package org.Project.Controller;

import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.Project.Apps;
import org.Project.DataBase.JdbcDao;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailIdField;
    @FXML private PasswordField passwordField;
    @FXML private Button submitButton;

    @FXML
    public void register(ActionEvent event) throws SQLException, IOException {
        Window owner = submitButton.getScene().getWindow();

        if (fullNameField.getText().trim().isEmpty() || emailIdField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Semua field harus diisi.", false);
            return;
        }

        String emailId = emailIdField.getText().trim();
        if (!isValidEmail(emailId)) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Format email tidak valid.", false);
            return;
        }

        String password = passwordField.getText().trim();
        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Password minimal 6 karakter.", false);
            return;
        }

        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Password harus mengandung huruf dan angka.", false);
            return;
        }

        JdbcDao jdbcDao = new JdbcDao();
        if (jdbcDao.isEmailExist(emailId)) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Email sudah terdaftar.", false);
            return;
        }

        jdbcDao.insertRecord(emailId, password);

        showAlert(Alert.AlertType.INFORMATION, owner, "Registrasi Berhasil!", "Selamat datang, " + fullNameField.getText().trim(), true);

        Apps.setRoot("login-view.fxml", "Login", true);
    }

    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message, boolean wait) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) {
            alert.initOwner(owner);
        }

        if (wait) {
            alert.showAndWait();
        } else {
            alert.show();
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
        return password.matches(passwordRegex);
    }
}
