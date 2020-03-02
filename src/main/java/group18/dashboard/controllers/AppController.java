package group18.dashboard.controllers;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import group18.dashboard.ViewDataParser;
import group18.dashboard.model.Campaign;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public Label importNotification;
    public StackPane chartPane;
    public Label impressions;
    public Label clicks;
    public Label uniques;
    public Label bounces;
    public Label conversions;
    public Label totalCost;
    public Label ctr;
    public Label cpa;
    public Label cpc;
    public Label cpm;
    public Label bounceRate;
    public JFXToggleButton impressionsButton;
    public JFXToggleButton clicksButton;
    public JFXToggleButton uniquesButton;
    public JFXToggleButton bouncesButton;
    public JFXToggleButton conversionsButton;
    public JFXToggleButton totalCostButton;
    public JFXToggleButton ctrButton;
    public JFXToggleButton cpaButton;
    public JFXToggleButton cpcButton;
    public JFXToggleButton cpmButton;
    public JFXToggleButton bounceRateButton;
    public TabPane tabs;

    Campaign in;

    @FXML
    public void initialize() {
        in = new Campaign();
    }

    @FXML
    public void themeButtonAction() {
    }

    @FXML
    public void importCampaignButtonAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Campaign Folder");
        File dir = directoryChooser.showDialog(appView.getScene().getWindow());
        if (dir == null) return;

        ExecutorService executor = Executors.newWorkStealingPool();

        executor.execute(() -> {
            try {
                in.readClicks(dir.getAbsolutePath());
                System.out.println("clicks");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.execute(() ->
        {
            try {
                in.readImpressions(dir.getAbsolutePath());
                System.out.println("impressions");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.execute(() -> {
            try {
                in.readInteractions(dir.getAbsolutePath());
                System.out.println("interactions");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
        chartPane.getChildren().remove(importNotification);
        mainChart.setVisible(true);
    }
}
