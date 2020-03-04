package group18.dashboard.controllers;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import group18.dashboard.ViewDataParser;
import group18.dashboard.model.Campaign;
import group18.dashboard.model.Click;
import group18.dashboard.model.Impression;
import group18.dashboard.model.Interaction;
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
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static group18.dashboard.model.Campaign.distinctByKey;

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
    public VBox initImport;

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
        impressionsButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getImpressionSeries());
            else seriesList.remove(campaign.getImpressionSeries());
        });
        clicksButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getClickCountSeries());
            else seriesList.remove(campaign.getClickCountSeries());
        });
        uniquesButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getUniquesSeries());
            else seriesList.remove(campaign.getUniquesSeries());
        });
        bouncesButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getBouncesSeries());
            else seriesList.remove(campaign.getBouncesSeries());
        });
        conversionsButton.selectedProperty().addListener((o, old, selected) -> {
            if (selected) seriesList.add(campaign.getConversionSeries());
            else seriesList.remove(campaign.getConversionSeries());
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
        });
        executor.shutdown();
        chartPane.getChildren().remove(initImport);
        mainChart.setVisible(true);
    }

    private void updateMetrics(ExecutorService executor) {
        System.out.println("--Calculating Metrics--");
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
        System.out.println("bounce rate... done");
    }

    private void updateBounces() {
        var bounces = ViewDataParser.getBounces(in.getInteractions());
        Platform.runLater(() -> in.setBounces(bounces));
        System.out.println("bounces... done");
    }

    private void updateClickCount() {
        var clicks = in.getClicks().size();
        Platform.runLater(() -> in.setClickCount(clicks));
        System.out.println("clicks... done");
    }

    private void updateConversions() {
        var conversions = ViewDataParser.getConversions(in.getInteractions());
        Platform.runLater(() -> in.setConversions(conversions));
        System.out.println("conversions... done");
    }

    private void updateCPA() {
        var cpa = ViewDataParser.getCPA(in.getImpressions(), in.getClicks(), in.getInteractions());
        Platform.runLater(() -> in.setCpa(cpa));
        System.out.println("CPA... done");
    }

    private void updateCPC() {
        var cpc = ViewDataParser.getCPC(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setCpc(cpc));
        System.out.println("CPC... done");
    }

    private void updateCPM() {
        var cpm = ViewDataParser.getCPM(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setCpm(cpm));
        System.out.println("CPM... done");
    }

    private void updateCTR() {
        var ctr = ViewDataParser.getCTR(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setCtr(ctr));
        System.out.println("CTR... done");
    }

    private void updateImpressions() {
        var impressions = in.getImpressions().size();
        Platform.runLater(() -> in.setImpressionCount(impressions));
        System.out.println("impressions... done");
    }

    private void updateTotalCost() {
        var totalCost = ViewDataParser.getTotalCost(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setTotalCost(totalCost));
        System.out.println("total cost... done");
    }

    private void updateUniques() {
        var uniques = ViewDataParser.getUniques(in.getClicks());
        Platform.runLater(() -> in.setUniques(uniques));
        System.out.println("uniques... done");
    }

    private void updateChartMetrics(ExecutorService executor, int resolution) {
        executor.execute(() -> updateBounceRateSeries(resolution));
        executor.execute(() -> updateCPASeries(resolution));
        executor.execute(() -> updateCPCSeries(resolution));
        executor.execute(this::updateCPMSeries);
        executor.execute(() -> updateCTRSeries(resolution));
        executor.execute(() -> updateTotalCostSeries(resolution));
        executor.execute(() -> updateClickCountSeries(resolution));
        executor.execute(() -> updateImpressionsSeries(resolution));
        executor.execute(() -> updateUniquesSeries(resolution));
        executor.execute(() -> updateBouncesSeries(resolution));
        executor.execute(() -> updateConversionsSeries(resolution));
    }

    private void updateBounceRateSeries(int resolution) {
        in.setBounceRateSeries(ViewDataParser.getBounceRateTimeSeries(resolution, in.getClicks(), in.getInteractions()));
        bounceRateButton.setDisable(false);
        System.out.println("[Series] bounce rate... done");
    }

    private void updateBouncesSeries(int resolution) {
        in.setBouncesSeries(ViewDataParser.getCumulativeTimeSeries("Bounces", resolution,
                in.getInteractions().parallelStream()
                        .filter(interaction -> !interaction.isConversion()).map(Interaction::getEntryDate).collect(Collectors.toList())));
        bouncesButton.setDisable(false);
        System.out.println("[Series] bounces... done");
    }

    private void updateClickCountSeries(int resolution) {
        in.setClickCountSeries(ViewDataParser.getCumulativeTimeSeries("Clicks", resolution,
                in.getClicks().parallelStream().map(Click::getDate).collect(Collectors.toList())));
        clicksButton.setDisable(false);
        System.out.println("[Series] clicks... done");
    }

    private void updateConversionsSeries(int resolution) {
        in.setConversionSeries(ViewDataParser.getCumulativeTimeSeries("Conversions", resolution,
                in.getInteractions().parallelStream()
                        .filter(Interaction::isConversion).map(Interaction::getEntryDate).collect(Collectors.toList())));
        conversionsButton.setDisable(false);
        System.out.println("[Series] conversions... done");
    }

    private void updateCPASeries(int resolution) {
        in.setCPASeries(ViewDataParser.getCPATimeSeries(resolution, in.getImpressions(), in.getClicks(), in.getInteractions()));
        //Platform.runLater(()->seriesList.add(in.getCPASeries()));
        cpaButton.setDisable(false);
        System.out.println("[Series] CPA... done");
    }

    private void updateCPCSeries(int resolution) {
        in.setCPCSeries(ViewDataParser.getCPCTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCPCSeries()));
        cpcButton.setDisable(false);
        System.out.println("[Series] CPC... done");
    }

    private void updateCPMSeries() {
        in.setCPMSeries(ViewDataParser.getCPMTimeSeries(in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCPMSeries()));
        cpmButton.setDisable(false);
        System.out.println("[Series] CPM... done");
    }

    private void updateCTRSeries(int resolution) {
        in.setCtrSeries(ViewDataParser.getTotalCostSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCtrSeries()));
        ctrButton.setDisable(false);
        System.out.println("[Series] CTR... done");
    }

    private void updateImpressionsSeries(int resolution) {
        in.setImpressionSeries(ViewDataParser.getCumulativeTimeSeries("Impressions", resolution,
                in.getImpressions().parallelStream().map(Impression::getDate).collect(Collectors.toList())));
        impressionsButton.setDisable(false);
        System.out.println("[Series] impressions... done");
    }

    private void updateTotalCostSeries(int resolution) {
        in.setTotalCostSeries(ViewDataParser.getCTRTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getTotalCostSeries()));
        totalCostButton.setDisable(false);
        System.out.println("[Series] total cost... done");
    }

    private void updateUniquesSeries(int resolution) {
        in.setUniquesSeries(ViewDataParser.getCumulativeTimeSeries("Uniques", resolution,
                in.getClicks().parallelStream().filter(distinctByKey(Click::getDate)).map(Click::getDate).collect(Collectors.toList())));
        uniquesButton.setDisable(false);
        System.out.println("[Series] uniques... done");
    }
}
