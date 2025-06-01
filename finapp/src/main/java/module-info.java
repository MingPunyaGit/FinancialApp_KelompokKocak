module org.example.finapp {
    // Izin yang dibutuhkan oleh JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs; // Dibutuhkan untuk fitur "Ingat Saya"

    // Buka akses ke paket 'models' agar TableView bisa membaca datanya
    opens org.example.finapp.models to javafx.base;

    // Buka akses ke paket 'controllers' agar FXML bisa terhubung
    opens org.example.finapp.controllers to javafx.fxml;

    // Ekspor paket utama agar aplikasi bisa dijalankan
    exports org.example.finapp;
}