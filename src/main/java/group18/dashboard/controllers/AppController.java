package group18.dashboard.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class AppController {

    public MenuItem themeButton;
    public MenuItem uiScalingButton;
    public MenuItem exportPNGButton;
    public MenuItem importCampaignButton;
    public BorderPane appView;
    public TabPane dashboard;
    @FXML
    private DashboardController dashboardController;

    @FXML
    public void initialize() {

    }

    @FXML
    public void themeButtonAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("oops..");
        alert.setHeaderText(null);
        alert.setContentText("This feature has not been implemented yet!");
        alert.showAndWait();
    }

    public void importCampaignButtonAction() {
        dashboardController.importCampaignButtonAction();
        //this should open a new tab with init (import) view
    }
}
