module group18.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.lang;
    requires com.jfoenix;

    opens group18.dashboard.controllers to javafx.fxml;
    exports group18.dashboard;
    exports group18.dashboard.model;
    exports group18.dashboard.controllers;
    exports group18.dashboard.exceptions;
}