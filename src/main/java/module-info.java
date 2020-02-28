module group18.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.lang;

    opens group18.dashboard to javafx.fxml;
    exports group18.dashboard;
}