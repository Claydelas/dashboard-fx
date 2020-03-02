package group18.dashboard.controllers;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import group18.dashboard.ViewDataParser;
import group18.dashboard.model.Campaign;
import javafx.application.Platform;
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
import java.util.concurrent.CountDownLatch;
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
        impressions.textProperty().bind(in.impressionCountProperty().asString());
        clicks.textProperty().bind(in.clickCountProperty().asString());
        uniques.textProperty().bind(in.uniquesProperty().asString());
        bounces.textProperty().bind(in.bouncesProperty().asString());
        conversions.textProperty().bind(in.conversionsProperty().asString());
        totalCost.textProperty().bind(in.totalCostProperty().asString());
        ctr.textProperty().bind(in.ctrProperty().asString());
        cpa.textProperty().bind(in.cpaProperty().asString());
        cpc.textProperty().bind(in.cpcProperty().asString());
        cpm.textProperty().bind(in.cpmProperty().asString());
        bounceRate.textProperty().bind(in.bounceRateProperty().asString());

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

        System.out.println("--Parsing Campaign--");
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(3);

        executor.execute(() -> {
            try {
                in.readClicks(dir.getAbsolutePath());
                System.out.println("Clicks parsed: " + in.getClicks().size());
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.execute(() ->
        {
            try {
                in.readImpressions(dir.getAbsolutePath());
                System.out.println("Impressions parsed: " + in.getImpressions().size());
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.execute(() -> {
            try {
                in.readInteractions(dir.getAbsolutePath());
                System.out.println("Interactions parsed: " + in.getInteractions().size());
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.execute(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> in.setBounceRate(ViewDataParser.getBounceRate(in.getClicks(), in.getInteractions())));
            Platform.runLater(() -> in.setBounces(ViewDataParser.getBounces(in.getInteractions())));
            Platform.runLater(() -> in.setClickCount(in.getClicks().size()));
            Platform.runLater(() -> in.setConversions(ViewDataParser.getConversions(in.getInteractions())));
            Platform.runLater(() -> in.setCpa(ViewDataParser.getCPA(in.getImpressions(), in.getClicks(), in.getInteractions())));
            Platform.runLater(() -> in.setCpc(ViewDataParser.getCPC(in.getImpressions(), in.getClicks())));
            Platform.runLater(() -> in.setCpm(ViewDataParser.getCPM(in.getImpressions(), in.getClicks())));
            Platform.runLater(() -> in.setCtr(ViewDataParser.getCTR(in.getImpressions(), in.getClicks())));
            Platform.runLater(() -> in.setImpressionCount(in.getImpressions().size()));
            Platform.runLater(() -> in.setTotalCost(ViewDataParser.getTotalCost(in.getImpressions(), in.getClicks())));
            Platform.runLater(() -> in.setUniques(ViewDataParser.getUniques(in.getClicks())));
            System.out.println("--Finished Parsing Campaign--");
        });
        executor.shutdown();
        chartPane.getChildren().remove(importNotification);
        mainChart.setVisible(true);
    }
}
