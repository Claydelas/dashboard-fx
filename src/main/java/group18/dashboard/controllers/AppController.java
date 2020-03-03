package group18.dashboard.controllers;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import group18.dashboard.ViewDataParser;
import group18.dashboard.model.Campaign;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppController {

    public JFXSlider granularitySlider;
    public NumberAxis yAxis;
    public LineChart<String, Number> mainChart;
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

    ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList();
    Property<ObservableList<XYChart.Series<String, Number>>> series = new SimpleListProperty<>(seriesList);

    Campaign in;

    @FXML
    public void initialize() {
        in = new Campaign();
        bindMetrics(in);
        bindChartMetrics(in);
    }

    private void bindMetrics(Campaign campaign) {
        impressions.textProperty().bind(campaign.impressionCountProperty().asString());
        clicks.textProperty().bind(campaign.clickCountProperty().asString());
        uniques.textProperty().bind(campaign.uniquesProperty().asString());
        bounces.textProperty().bind(campaign.bouncesProperty().asString());
        conversions.textProperty().bind(campaign.conversionsProperty().asString());
        totalCost.textProperty().bind(campaign.totalCostProperty().asString());
        ctr.textProperty().bind(campaign.ctrProperty().asString());
        cpa.textProperty().bind(campaign.cpaProperty().asString());
        cpc.textProperty().bind(campaign.cpcProperty().asString());
        cpm.textProperty().bind(campaign.cpmProperty().asString());
        bounceRate.textProperty().bind(campaign.bounceRateProperty().asString());
    }

    private void bindChartMetrics(Campaign campaign) {
        mainChart.dataProperty().bind(series);
        mainChart.setAnimated(false);

        totalCostButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getTotalCostSeries());
            else seriesList.remove(campaign.getTotalCostSeries());
        });
        ctrButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getCtrSeries());
            else seriesList.remove(campaign.getCtrSeries());
        });
        cpmButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getCPMSeries());
            else seriesList.remove(campaign.getCPMSeries());
        });
        cpaButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getCPASeries());
            else seriesList.remove(campaign.getCPASeries());
        });
        cpcButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getCPCSeries());
            else seriesList.remove(campaign.getCPCSeries());
        });
        bounceRateButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getBounceRateSeries());
            else seriesList.remove(campaign.getBounceRateSeries());
        });
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
            updateMetrics(executor);
            updateChartMetrics(executor, Calendar.DAY_OF_MONTH);
            System.out.println("--Finished Parsing Campaign--");
        });
        executor.shutdown();
        chartPane.getChildren().remove(importNotification);
        mainChart.setVisible(true);
    }

    private void updateMetrics(ExecutorService executor) {
        executor.execute(this::updateBounceRate);
        executor.execute(this::updateBounces);
        executor.execute(this::updateClickCount);
        executor.execute(this::updateConversions);
        executor.execute(this::updateCPA);
        executor.execute(this::updateCPC);
        executor.execute(this::updateCPM);
        executor.execute(this::updateCTR);
        executor.execute(this::updateImpressions);
        executor.execute(this::updateTotalCost);
        executor.execute(this::updateUniques);
    }

    private void updateBounceRate() {
        var bounceRate = ViewDataParser.getBounceRate(in.getClicks(), in.getInteractions());
        Platform.runLater(() -> in.setBounceRate(bounceRate));
    }

    private void updateBounces() {
        var bounces = ViewDataParser.getBounces(in.getInteractions());
        Platform.runLater(() -> in.setBounces(bounces));
    }

    private void updateClickCount() {
        var clicks = in.getClicks().size();
        Platform.runLater(() -> in.setClickCount(clicks));
    }

    private void updateConversions() {
        var conversions = ViewDataParser.getConversions(in.getInteractions());
        Platform.runLater(() -> in.setConversions(conversions));
    }

    private void updateCPA() {
        var cpa = ViewDataParser.getCPA(in.getImpressions(), in.getClicks(), in.getInteractions());
        Platform.runLater(() -> in.setCpa(cpa));
    }

    private void updateCPC() {
        var cpc = ViewDataParser.getCPC(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setCpc(cpc));
    }

    private void updateCPM() {
        var cpm = ViewDataParser.getCPM(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setCpm(cpm));
    }

    private void updateCTR() {
        var ctr = ViewDataParser.getCTR(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setCtr(ctr));
    }

    private void updateImpressions() {
        var impressions = in.getImpressions().size();
        Platform.runLater(() -> in.setImpressionCount(impressions));
    }

    private void updateTotalCost() {
        var totalCost = ViewDataParser.getTotalCost(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setTotalCost(totalCost));
    }

    private void updateUniques() {
        var uniques = ViewDataParser.getUniques(in.getClicks());
        Platform.runLater(() -> in.setUniques(uniques));
    }

    private void updateChartMetrics(ExecutorService executor, int resolution) {
        executor.execute(() -> updateBounceRateSeries(resolution));
        executor.execute(() -> updateCPASeries());
        executor.execute(() -> updateCPCSeries(resolution));
        executor.execute(() -> updateCPMSeries());
        executor.execute(() -> updateCTRSeries(resolution));
        executor.execute(() -> updateTotalCostSeries(resolution));

    }

    private void updateBounceRateSeries(int resolution) {
        in.setBounceRateSeries(ViewDataParser.getBounceRateTimeSeries(resolution, in.getClicks(), in.getInteractions()));
        Platform.runLater(() -> {
            bounceRateButton.setDisable(false);
            //seriesList.add(in.getBounceRateSeries());
        });

    }

    private void updateBouncesSeries() {
    }

    private void updateClickCountSeries() {
    }

    private void updateConversionsSeries() {
    }

    private void updateCPASeries() {
        in.setCPASeries(ViewDataParser.getCPATimeSeries(in.getImpressions(), in.getClicks(), in.getInteractions()));
        //Platform.runLater(()->seriesList.add(in.getCPASeries()));
        cpaButton.setDisable(false);
    }

    private void updateCPCSeries(int resolution) {
        in.setCPCSeries(ViewDataParser.getCPCTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCPCSeries()));
        cpcButton.setDisable(false);
    }

    private void updateCPMSeries() {
        in.setCPMSeries(ViewDataParser.getCPMTimeSeries(in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCPMSeries()));
        cpmButton.setDisable(false);
    }

    private void updateCTRSeries(int resolution) {
        in.setCtrSeries(ViewDataParser.getTotalCostSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCtrSeries()));
        ctrButton.setDisable(false);
    }

    private void updateImpressionsSeries() {
    }

    private void updateTotalCostSeries(int resolution) {
        in.setTotalCostSeries(ViewDataParser.getCTRTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getTotalCostSeries()));
        totalCostButton.setDisable(false);
    }

    private void updateUniquesSeries() {
    }
}
