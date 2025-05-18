module org.example.finapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.finapp to javafx.fxml;
    opens org.example.finapp.controllers to javafx.fxml;
    exports org.example.finapp;
    exports org.example.finapp.controllers;  // Baris penting yang ditambahkan
}