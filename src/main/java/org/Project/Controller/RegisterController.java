package org.Project.Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.Project.Database.DB;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private PasswordField pass1;
    @FXML private PasswordField pass2;
    @FXML private TextField pet;
    @FXML private TextField food;
    @FXML private TextField book;
    @FXML private ColorPicker color;
    @FXML private Button submitButton;
    @FXML private Hyperlink login;
    @FXML
    private Label labelPasswordInfo;


    @FXML
    void register(ActionEvent event) throws IOException {
        Window owner = submitButton.getScene().getWindow();

        String username = fullNameField.getText().trim();
        String password = pass1.getText().trim();
        String confirmPassword = pass2.getText().trim();
        String petAnswer = pet.getText().trim();
        String foodAnswer = food.getText().trim();
        String bookAnswer = book.getText().trim();
        Color colorValue = color.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                petAnswer.isEmpty() || foodAnswer.isEmpty() || bookAnswer.isEmpty() || colorValue == null) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Semua field harus diisi.", false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Password dan Konfirmasi tidak sesuai.", false);
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Password minimal 6 karakter.", false);
            return;
        }

        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Password harus mengandung huruf dan angka.", false);
            return;
        }

        DB jdbcDao = new DB();
        if (jdbcDao.isUsernameExist(username)) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Username sudah terdaftar.", false);
            return;
        }

        boolean inserted = jdbcDao.insertUser(username, password, petAnswer, foodAnswer, bookAnswer, colorValue.toString());
        if (inserted) {
            showAlert(Alert.AlertType.INFORMATION, owner, "Registrasi Berhasil!", "Hallo, " + username + "!", true);

            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/login-view.fxml"));
            Stage loginStage = new Stage();
            Scene scene = new Scene(loader.load());
            loginStage.setTitle("Login");
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.show();
        } else {
            showAlert(Alert.AlertType.ERROR, owner, "Registrasi Gagal!", "Terjadi kesalahan saat menyimpan data.", false);
        }
    }

    @FXML
    void onactionlg(ActionEvent event) throws IOException {
        Stage stage = (Stage) login.getScene().getWindow();
        stage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/login-view.fxml"));
        Stage loginStage = new Stage();
        Scene scene = new Scene(loader.load());
        loginStage.setTitle("Login");
        loginStage.setScene(scene);
        loginStage.setResizable(false);
        loginStage.show();
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
        return password.matches(passwordRegex);
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

    public void onClickPass(ActionEvent actionEvent) {
        labelPasswordInfo.setText("Minimal password 8 - 16 karakter, harus mengandung minimal 1 angka dan 1 karakter spesial.");
        labelPasswordInfo.setVisible(true);
    }
}
