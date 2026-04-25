module com.example.footballticketmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.footballticketmanager to javafx.fxml;
    opens com.example.footballticketmanager.controller to javafx.fxml;

    exports com.example.footballticketmanager;
}
