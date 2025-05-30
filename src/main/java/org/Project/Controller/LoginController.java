package org.Project.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.Project.Database.UserDB;
import org.Project.Manager.Session;
import javafx.scene.Parent;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;

    @FXML
    void handleLogin(ActionEvent event) {
        Window owner = loginButton.getScene().getWindow();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Login Gagal", "Username dan Password harus diisi.", false);
            return;
        }

        UserDB userDb = new UserDB();
        if (userDb.validateLogin(username, password)) {
            int userId = userDb.getUserId(username);
            Session.getInstance().setSession(userId, username);

            showAlert(Alert.AlertType.INFORMATION, owner, "Login Berhasil", "Selamat datang, " + username, false);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/TampilanWel-view.fxml"));
                Parent root = loader.load();
                TampilanWelController controller = loader.getController();
                controller.setUserId(userId);
                Stage stage = new Stage();
                stage.setTitle("Dashboard");
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
                stage.show();

                ((Stage) loginButton.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, owner, "Error", "Gagal membuka dashboard.", false);
            }
        } else {
            showAlert(Alert.AlertType.ERROR, owner, "Login Gagal", "Username atau Password salah.", false);
        }
    }

    @FXML
    void handleRegisterLink(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/Registrasi-view.fxml"));
            Parent root = loader.load();

            Stage regStage = new Stage();
            regStage.setTitle("Register");
            regStage.setScene(new Scene(root));
            regStage.setMaximized(true);
            regStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Gagal membuka halaman registrasi");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleForgotPassword(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/ForgetPass-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Lupa Password");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Gagal memuat ForgetPass-view.fxml");
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Gagal membuka halaman reset password");
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
}
