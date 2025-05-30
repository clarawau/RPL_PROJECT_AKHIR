package org.Project.Controller;

import org.Project.Database.UserDB;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;

public class ForgetPassController {
    @FXML private TextField usernameField;
    @FXML private TextField petField;
    @FXML private TextField foodField;
    @FXML private TextField bookField;
    @FXML private ColorPicker colorPicker;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button resetButton;
    @FXML private Hyperlink backToLogin;

    @FXML
    void resetPassword(ActionEvent event) {
        Window owner = resetButton.getScene().getWindow();
        String username = usernameField.getText().trim();
        String pet = petField.getText().trim().toLowerCase();
        String food = foodField.getText().trim().toLowerCase();
        String book = bookField.getText().trim().toLowerCase();
        String color = "";

        if (colorPicker.getValue() != null) {
            color = colorPicker.getValue().toString().trim().toLowerCase(); // pastikan konsisten dengan database
        }

        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validasi isian kosong
        if (username.isEmpty() || pet.isEmpty() || food.isEmpty() || book.isEmpty() || color.isEmpty()
                || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Semua field harus diisi.", false);
            return;
        }

        // Validasi kecocokan password
        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, owner, "Password Error", "Password tidak cocok.", false);
            return;
        }

        // Validasi kompleksitas password
        if (newPassword.length() < 8 && newPassword.length() > 16 || !isValidPassword(newPassword)) {
            showAlert(Alert.AlertType.ERROR, owner, "ok","Password harus 8-16 karakter, mengandung huruf besar, huruf kecil, angka, dan karakter spesial.", false);
            return;
        }

        // Verifikasi jawaban keamanan
        UserDB dao = new UserDB();
        boolean verified = dao.verifySecurityAnswers(username, pet, food, book, color);
        if (!verified) {
            showAlert(Alert.AlertType.ERROR, owner, "Verifikasi Gagal", "Jawaban security question salah.", false);
            return;
        }

        // Update password
        dao.updatePassword(username, newPassword);
        showAlert(Alert.AlertType.INFORMATION, owner, "Berhasil", "Password berhasil diperbarui!", true);

//        // Kembali ke login kok 2 ya mb clara??
//        try {
//            Stage stage = (Stage) resetButton.getScene().getWindow();
//            stage.close();
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/login-view.fxml"));
//            Stage loginStage = new Stage();
//            Scene scene = new Scene(loader.load());
//            loginStage.setTitle("Login");
//            loginStage.setScene(scene);
//            loginStage.setMaximized(true);
//            loginStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$";
        return password.matches(regex);
    }

    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message, boolean wait) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        if (wait) {
            alert.showAndWait();
        } else {
            alert.show();
        }
    }

    public void backToLogin(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) backToLogin.getScene().getWindow();
        stage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/login-view.fxml"));
        Stage loginStage = new Stage();
        Scene scene = new Scene(loader.load());
        loginStage.setTitle("Login");
        loginStage.setScene(scene);
        loginStage.setMaximized(true);
        loginStage.show();
    }
}