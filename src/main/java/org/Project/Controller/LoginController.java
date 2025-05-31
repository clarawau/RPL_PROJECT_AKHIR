package org.Project.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.Project.Database.UserDB;
import org.Project.Manager.Session;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;

    // Fokus awal diarahkan ke container, bukan TextField
    @FXML private VBox loginContainer;

    @FXML
    void handleLogin(ActionEvent event) {
        Window owner = loginButton.getScene().getWindow();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Login failed!", "Username and Password must be filled!", false);
            return;
        }

        UserDB userDb = new UserDB();
        if (userDb.validateLogin(username, password)) {
            int userId = userDb.getUserId(username);
            Session.getInstance().setSession(userId, username);

//            showAlert(Alert.AlertType.INFORMATION, owner, "Login Successfu", "Welcome, " + username, false);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/tampilanHome-view.fxml"));
                Parent root = loader.load();
                TampilanHomeController controller = loader.getController();
                controller.setUserId(userId);

                Stage stage = new Stage();
                stage.setTitle("Home");
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();

                ((Stage) loginButton.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, owner, "Error!", "Failed open homepage!", false);
            }
        } else {
            showAlert(Alert.AlertType.ERROR, owner, "Login Failed!", "Incorrect username or password!", false);
        }
    }

    @FXML
    void handleRegisterLink(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/signup-view.fxml"));
            Parent root = loader.load();

            Stage regStage = new Stage();
            regStage.setTitle("Sign Up");
            regStage.setScene(new Scene(root));
            regStage.setMaximized(true);
            regStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed open sign up page");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleForgotPassword(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/forgetPass-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Reset Password");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Failed load forgetPass-view.fxml!");
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Failed open reset password page!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
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

    @FXML
    public void initialize() {
        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");

        setupPlaceholder(usernameField, "Username");
        setupPlaceholder(passwordField, "Password");

        // Fokus awal diarahkan ke VBox, supaya TextField tidak langsung difokus (tidak muncul border biru)
        Platform.runLater(() -> loginContainer.requestFocus());
    }

    private void setupPlaceholder(TextField field, String prompt) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && field.getText().isEmpty()) {
                field.setPromptText("");
            } else if (!newVal && field.getText().isEmpty()) {
                field.setPromptText(prompt);
            }
        });
    }
}
