package org.Project.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.Project.DB.DB;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    void handleLogin(ActionEvent event) {
        Window owner = loginButton.getScene().getWindow();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Login Gagal", "Username dan Password harus diisi.", false);
            return;
        }

        DB dao = new DB();
        boolean valid = dao.validateLogin(username, password);

        if (valid) {
            showAlert(Alert.AlertType.INFORMATION, owner, "Login Berhasil", "Selamat datang, " + username, false);
            try {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/TampilanAwal.fxml"));
                Stage homeStage = new Stage();
                Scene scene = new Scene(loader.load());
                homeStage.setTitle("Dashboard");
                homeStage.setScene(scene);
                homeStage.setResizable(false);
                homeStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, owner, "Login Gagal", "Username atau Password salah.", false);
        }
    }

    @FXML
    void handleRegisterLink(ActionEvent event) {
        try {
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/register-view.fxml"));
            Stage regStage = new Stage();
            Scene scene = new Scene(loader.load());
            regStage.setTitle("Register");
            regStage.setScene(scene);
            regStage.setResizable(false);
            regStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleForgotPasswordLink(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/ForgetPass-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Lupa Password");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            System.err.println("Gagal memuat forgot-password-view.fxml");
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Gagal membuka halaman reset password");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void handleForgotPassword(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/Project/ForgetPass-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Lupa Password");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            System.err.println("Gagal memuat forgot-password-view.fxml");
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
