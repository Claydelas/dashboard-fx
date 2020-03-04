package group18.dashboard.controllers;

import group18.dashboard.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class ImportController {

    public Button importButton;

    @FXML
    private void importCampaign() throws IOException {
        App.setRoot("app");
    }
}