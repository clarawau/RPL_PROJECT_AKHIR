package org.Project;

import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailIdField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField nicknameField;
    @FXML private ColorPicker favoriteColorPicker;
    @FXML private DatePicker birthDatePicker;
    @FXML private Button submitButton;
    @FXML
    public void register(ActionEvent event) throws SQLException, IOException {
        Window owner = submitButton.getScene().getWindow();


        if (fullNameField.getText().isEmpty() ||
                emailIdField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Semua field harus diisi.", false);
            return;
        }


        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, owner, "Password Mismatch", "Password dan Konfirmasi Password tidak sama.", false);
            return;
        }


        String fullName = fullNameField.getText();
        String emailId = emailIdField.getText();
        String password = passwordField.getText();
        String nickname = nicknameField.getText();
        String favoriteColor = (favoriteColorPicker.getValue() != null) ? favoriteColorPicker.getValue().toString() : "";
        String birthDate = (birthDatePicker.getValue() != null) ? birthDatePicker.getValue().toString() : "";


        JdbcDao jdbcDao = new JdbcDao();
        jdbcDao.createUsersTableIfNotExists();
        jdbcDao.insertRecord(fullName, emailId, password, nickname, favoriteColor, birthDate);
        showAlert(Alert.AlertType.INFORMATION, owner, "Registrasi Berhasil!", "Selamat datang, " + fullName, true);
        Apps.setRoot("login-view", "Login", true);
    }


    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message, boolean wait) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) {
            alert.initOwner(owner);
        }

        if (wait) alert.showAndWait();
        else alert.show();
    }

    @FXML
    private void goToLogin() throws IOException {
        Apps.setRoot("login-view", "Login", true);
    }
}
