module group18.dashboard {
    requires javafx.controls;
    requires javafx.fxml;

    opens group18.dashboard to javafx.fxml;
    exports group18.dashboard;
}