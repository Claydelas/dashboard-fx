module group18.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.lang;
    requires com.jfoenix;

    opens group18.dashboard.controllers to javafx.fxml;
    exports group18.dashboard;
}