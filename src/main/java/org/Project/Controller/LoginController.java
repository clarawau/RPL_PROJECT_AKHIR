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
    @FXML private VBox loginContainer;

    @FXML
    void handleLogin(ActionEvent event) {
        Window owner = loginButton.getScene().getWindow();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Login Failed!", "Username and Password must be filled!", false);
            return;
        }

        UserDB userDb = new UserDB();
        if (userDb.validateLogin(username, password)) {
            int userId = userDb.getUserId(username);


            Session.getInstance().setSession(userId, username);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/tampilanHome-view.fxml"));
                Parent root = loader.load();


                TampilanHomeController controller = loader.getController();
                controller.setUserId(userId);
                controller.setUsername(username);

                Stage stage = new Stage();
                stage.setTitle("Home");
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();


                ((Stage) loginButton.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, owner, "Error!", "Failed to open home page!", false);
            }
        } else {
            showAlert(Alert.AlertType.ERROR, owner, "Login Failed!", "Incorrect username or password!", false);
        }
    }

    @FXML
    void handleRegisterLink(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/signup-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Sign Up");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, loginButton.getScene().getWindow(), "Error", "Failed to open sign up page", false);
        }
    }

    @FXML
    public void handleForgotPassword(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/forgetPass-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Reset Password");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, loginButton.getScene().getWindow(), "Error!", "Failed to open reset password page!", false);
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
