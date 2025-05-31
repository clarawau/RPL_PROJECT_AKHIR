package org.Project.Controller;

import org.Project.Database.UserDB;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;

public class SignUpController {

    @FXML private TextField fullNameField;
    @FXML private PasswordField pass1;
    @FXML private PasswordField pass2;
    @FXML private TextField pet;
    @FXML private TextField food;
    @FXML private TextField book;
    @FXML private ColorPicker color;
    @FXML private Button submitButton;
    @FXML private Hyperlink login;

    @FXML private Label labelPasswordInfo;
    @FXML private Label labelConfirmPasswordWarning;

    @FXML
    public void initialize() {
        // Validasi password saat mengetik
        pass1.textProperty().addListener((obs, oldText, newText) -> {
            labelPasswordInfo.setVisible(true);
            if (!isValidPassword(newText)) {
                labelPasswordInfo.setText("Password must be 8–16 characters long and include uppercase letters, lowercase letters, numbers, and special characters.");
            } else {
                labelPasswordInfo.setVisible(false);
            }
        });

        // Validasi konfirmasi password
        pass2.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.equals(pass1.getText())) {
                labelConfirmPasswordWarning.setText("Password confirmation does not match.");
                labelConfirmPasswordWarning.setVisible(true);
            } else {
                labelConfirmPasswordWarning.setVisible(false);
            }
        });
    }

    @FXML
    void register(ActionEvent event) {
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
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "all field must be filled", false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Password and confirm password doesnt match", false);
            return;
        }

        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Password must be 8–16 characters long and include uppercase letters, lowercase letters, numbers, and special characters.", false);
            return;
        }

        UserDB jdbcDao = new UserDB();
        if (jdbcDao.isUsernameExist(username)) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Username has been registered", false);
            return;
        }

        boolean inserted = jdbcDao.insertUser(username, password, petAnswer, foodAnswer, bookAnswer, colorValue.toString());

        if (inserted) {
            showAlert(Alert.AlertType.INFORMATION, owner, "Registration Successful!", "Hello, " + username + "!" + "" + "Log in to access your account", true);
            openLoginWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, owner, "sign up failed!", "something wrong when saving your data", false);
        }
    }

    @FXML
    void onactionlg(ActionEvent event) {
        openLoginWindow();
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$");
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

    private void openLoginWindow() {
        try {
            Stage currentStage = (Stage) submitButton.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/login-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(scene);
            loginStage.setMaximized(true);
            loginStage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, null, "Error", "failed open login page: " + e.getMessage(), false);
        }
    }
}
