package group18.dashboard.controllers;

import com.jfoenix.controls.JFXSlider;
import group18.dashboard.Gender;
import group18.dashboard.Impression;
import group18.dashboard.Income;
import group18.dashboard.ViewDataParser;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppController {

    public JFXSlider granularitySlider;
    public NumberAxis yAxis;
    public LineChart mainChart;
    public MenuItem themeButton;
    public CategoryAxis xAxis;
    public MenuItem uiScalingButton;
    public MenuItem exportPNGButton;
    public MenuItem importCampaignButton;
    public BorderPane appView;


    @FXML
    public void initialize() {
        List<Impression> impressions = new ArrayList<>();
        /*for (int i = 0; i < 1000; i++) {
            impressions.add(new Impression(Date.from(Instant.now()),"2", Gender.MALE,20, Income.HIGH,20));
        }*/
        mainChart.getData().add(ViewDataParser.getCPMTimeSeries(impressions));
    }

    @FXML
    public void themeButtonAction() {
    }

    @FXML
    public void importCampaignButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Campaign Files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Log Files", "*.csv"));
        List<File> campaignFiles = fileChooser.showOpenMultipleDialog(appView.getScene().getWindow());
    }
}
