module org.example.finapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.prefs;

    opens org.example.finapp to javafx.fxml;
    opens org.example.finapp.controllers to javafx.fxml;
    opens org.example.finapp.models to javafx.base;

    exports org.example.finapp;
}