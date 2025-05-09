module org.Project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens org.Project to javafx.fxml;
    exports org.Project;
}

