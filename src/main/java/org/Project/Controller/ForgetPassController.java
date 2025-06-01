package org.Project.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.Project.Database.UserDB;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    @FXML private VBox resetContainer;

    @FXML
    public void initialize() {
        setupPlaceholder(usernameField, "Username");
        setupPlaceholder(petField, "Pet Name");
        setupPlaceholder(bookField, "Favorite Book");
        setupPlaceholder(foodField, "Favorite Food");
        setupPlaceholder(newPasswordField, "New Password");
        setupPlaceholder(confirmPasswordField, "Confirm Password");

        List<TextField> allFields = Arrays.asList(
                usernameField, petField, bookField,
                foodField, newPasswordField, confirmPasswordField
        );

        for (TextField field : allFields) {
            field.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    field.setStyle("-fx-border-color: #1e88e5; -fx-border-width: 2px; -fx-background-radius: 6; -fx-border-radius: 6;");
                } else {
                    field.setStyle("-fx-border-color: #cfd8dc; -fx-border-width: 1.5px; -fx-background-radius: 6; -fx-border-radius: 6;");
                }
            });
        }


        Platform.runLater(() -> {
            if (resetContainer != null) resetContainer.requestFocus();
        });
    }

    private void setupPlaceholder(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && field.getText().isEmpty()) {
                field.setPromptText("");
            } else if (!newVal && field.getText().isEmpty()) {
                field.setPromptText(prompt);
            }
        });
    }

    @FXML
    void resetPassword(ActionEvent event) {
        Window owner = resetButton.getScene().getWindow();
        String username = usernameField.getText().trim();
        String pet = petField.getText().trim().toLowerCase();
        String food = foodField.getText().trim().toLowerCase();
        String book = bookField.getText().trim().toLowerCase();
        String color = "";

        if (colorPicker.getValue() != null) {
            color = colorPicker.getValue().toString().trim().toLowerCase();
        }

        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || pet.isEmpty() || food.isEmpty() || book.isEmpty() || color.isEmpty()
                || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "all field must be filled.", false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, owner, "Password Error", "Password doesn't match.", false);
            return;
        }

        if ((newPassword.length() < 8 || newPassword.length() > 16) || !isValidPassword(newPassword)) {
            showAlert(Alert.AlertType.ERROR, owner, "Password Error",
                    "Password must contain 8-16 character, uppercase, lowercase, number, dan special character.", false);
            return;
        }

        UserDB dao = new UserDB();
        boolean verified = dao.verifySecurityAnswers(username, pet, food, book, color);
        if (!verified) {
            showAlert(Alert.AlertType.ERROR, owner, "verification failed", "security question answer is wrong.", false);
            return;
        }

        dao.updatePassword(username, newPassword);
        showAlert(Alert.AlertType.INFORMATION, owner, "Success!", "Password has been re-new!", true);
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
