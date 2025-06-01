package org.Project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.Project.Controller.TampilanHomeController;
import org.Project.Database.UserDB;
import org.Project.Manager.LoginSession;
import org.Project.Manager.Session;

import java.io.IOException;

public class Apps extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        new UserDB();

        String username = LoginSession.loadSession();
        FXMLLoader loader;

        if (username != null) {
            UserDB userDb = new UserDB();
            int userId = userDb.getUserId(username);
            Session.getInstance().setSession(userId, username);
            loader = new FXMLLoader(getClass().getResource("/org/Project/tampilanHome-view.fxml"));
            Parent root = loader.load();
            TampilanHomeController controller = loader.getController();
            controller.setUserId(userId);
            controller.setUsername(username);
            stage.setTitle("Home");
            stage.setScene(new Scene(root));
        } else {
            loader = new FXMLLoader(getClass().getResource("/org/Project/login-view.fxml"));
            Parent root = loader.load();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
        }
        stage.setMaximized(true);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
