module org.Project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens org.Project to javafx.fxml;
    exports org.Project;
    exports org.Project.Controller;
    opens org.Project.Controller to javafx.fxml;
    exports org.Project.DB;
    opens org.Project.DB to javafx.fxml;
    exports org.Project.model;
    opens org.Project.model to javafx.fxml;
}

