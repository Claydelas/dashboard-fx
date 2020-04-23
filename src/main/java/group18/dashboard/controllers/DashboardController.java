package group18.dashboard.controllers;

import com.jfoenix.controls.JFXComboBox;
import group18.dashboard.App;
import group18.dashboard.ViewDataParser;
import group18.dashboard.model.Campaign;
import group18.dashboard.model.Interaction;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class DashboardController {

    public NumberAxis yAxis;
    public CategoryAxis xAxis;
    public StackPane mainView;
    public BorderPane chartPane;
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
    public VBox initView;
    public StackPane loadingProgress;
    public TabPane tabs;
    public JFXComboBox<String> timeGranularity;
    public FlowPane dashboardArea;

    ObservableList<XYChart.Series<String, Number>> seriesList = FXCollections.observableArrayList();
    Property<ObservableList<XYChart.Series<String, Number>>> series = new SimpleListProperty<>(seriesList);
    Campaign in;
    private CountDownLatch importProgress;

    @FXML
    public void initialize() {
        in = new Campaign();

        timeGranularity.setItems(FXCollections.observableArrayList("Hours", "Days", "Weeks"));
        timeGranularity.getSelectionModel().clearAndSelect(1);
        timeGranularity.setTooltip(new Tooltip("Time granularity of the chart"));

        Button debug = new Button("debug");
        mainView.getChildren().add(debug);
        StackPane.setAlignment(debug, Pos.TOP_RIGHT);
        debug.setOnMouseClicked(e -> {
            mainView.getChildren().remove(initView);
            mainView.getChildren().remove(debug);
            chartPane.setVisible(true);
        });

        dashboardArea.prefWrapLengthProperty().bind(dashboardArea.widthProperty());

        //bindMetrics(in);
        //bindChartMetrics(in);
    }

    @FXML
    public void importCampaignButtonAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("import.fxml"));
            Parent importForm = fxmlLoader.load();
            ImportController importController = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(importForm));
            stage.sizeToScene();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindMetrics(Campaign campaign) {
        impressions.textProperty().bind(campaign.impressionCountProperty().asString("Impressions: %,d"));
        clicks.textProperty().bind(campaign.clickCountProperty().asString("Clicks: %,d"));
        uniques.textProperty().bind(campaign.uniquesProperty().asString("Uniques: %,d"));
        bounces.textProperty().bind(campaign.bouncesProperty().asString("Bounces: %,d"));
        conversions.textProperty().bind(campaign.conversionsProperty().asString("Conversions: %,d"));
        totalCost.textProperty().bind(campaign.totalCostProperty().asString("Total Cost: \u00A3%.2f"));
        ctr.textProperty().bind(campaign.ctrProperty().asString("Click-through-rate: %.2f%%"));
        cpa.textProperty().bind(campaign.cpaProperty().asString("Cost-per-acquisition: \u00A3%.2f"));
        cpc.textProperty().bind(campaign.cpcProperty().asString("Cost-per-click: \u00A3%.2f"));
        cpm.textProperty().bind(campaign.cpmProperty().asString("Cost-per-mille: \u00A3%.2f"));
        bounceRate.textProperty().bind(campaign.bounceRateProperty().asString("Bounce Rate: %.2f%%"));
    }

    public void updateMetrics(ExecutorService executor) {
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
        importProgress.countDown();
    }

    private void updateBounces() {
        var bounces = ViewDataParser.getBounces(in.getInteractions());
        Platform.runLater(() -> in.setBounces(bounces));
        System.out.println("bounces... done");
        importProgress.countDown();
    }

    private void updateClickCount() {
        var clicks = in.getClicks().size();
        Platform.runLater(() -> in.setClickCount(clicks));
        System.out.println("clicks... done");
        importProgress.countDown();
    }

    private void updateConversions() {
        var conversions = ViewDataParser.getConversions(in.getInteractions());
        Platform.runLater(() -> in.setConversions(conversions));
        System.out.println("conversions... done");
        importProgress.countDown();
    }

    private void updateCPA() {
        var cpa = ViewDataParser.getCPA(in.getImpressions(), in.getClicks(), in.getInteractions());
        Platform.runLater(() -> in.setCpa(cpa));
        System.out.println("CPA... done");
        importProgress.countDown();
    }

    private void updateCPC() {
        var cpc = ViewDataParser.getCPC(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setCpc(cpc));
        System.out.println("CPC... done");
        importProgress.countDown();
    }

    private void updateCPM() {
        var cpm = ViewDataParser.getCPM(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setCpm(cpm));
        System.out.println("CPM... done");
        importProgress.countDown();
    }

    private void updateCTR() {
        var ctr = ViewDataParser.getCTR(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setCtr(ctr));
        System.out.println("CTR... done");
        importProgress.countDown();
    }

    private void updateImpressions() {
        var impressions = in.getImpressions().size();
        Platform.runLater(() -> in.setImpressionCount(impressions));
        System.out.println("impressions... done");
        importProgress.countDown();
    }

    private void updateTotalCost() {
        var totalCost = ViewDataParser.getTotalCost(in.getImpressions(), in.getClicks());
        Platform.runLater(() -> in.setTotalCost(totalCost));
        System.out.println("total cost... done");
        importProgress.countDown();
    }

    private void updateUniques() {
        var uniques = ViewDataParser.getUniques(in.getClicks());
        Platform.runLater(() -> in.setUniques(uniques));
        System.out.println("uniques... done");
        importProgress.countDown();
    }

    private void updateBounceRateSeries(int resolution) {
        in.setBounceRateSeries(ViewDataParser.getBounceRateTimeSeries(resolution, in.getClicks(), in.getInteractions()));
        //bounceRateButton.setDisable(false);
        System.out.println("[Series] bounce rate... done");
        importProgress.countDown();
    }

    private void updateBouncesSeries(int resolution) {
        in.setBouncesSeries(ViewDataParser.getCumulativeTimeSeries("Bounces", resolution,
                in.getInteractions().parallelStream()
                        .filter(interaction -> !interaction.isConversion()).map(Interaction::getEntryDate).collect(Collectors.toList())));
        //bouncesButton.setDisable(false);
        System.out.println("[Series] bounces... done");
        importProgress.countDown();
    }


    private void updateConversionsSeries(int resolution) {
        in.setConversionSeries(ViewDataParser.getCumulativeTimeSeries("Conversions", resolution,
                in.getInteractions().parallelStream()
                        .filter(Interaction::isConversion).map(Interaction::getEntryDate).collect(Collectors.toList())));
        //conversionsButton.setDisable(false);
        System.out.println("[Series] conversions... done");
        importProgress.countDown();
    }

    private void updateCPASeries(int resolution) {
        in.setCPASeries(ViewDataParser.getCPATimeSeries(resolution, in.getImpressions(), in.getClicks(), in.getInteractions()));
        //Platform.runLater(()->seriesList.add(in.getCPASeries()));
        //cpaButton.setDisable(false);
        System.out.println("[Series] CPA... done");
        importProgress.countDown();
    }

    private void updateCPCSeries(int resolution) {
        in.setCPCSeries(ViewDataParser.getCPCTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCPCSeries()));
        //cpcButton.setDisable(false);
        System.out.println("[Series] CPC... done");
        importProgress.countDown();
    }

    private void updateCPMSeries(int resolution) {
        in.setCPMSeries(ViewDataParser.getCPMTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCPMSeries()));
        //cpmButton.setDisable(false);
        System.out.println("[Series] CPM... done");
        importProgress.countDown();
    }

    private void updateCTRSeries(int resolution) {
        in.setCtrSeries(ViewDataParser.getCTRTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCtrSeries()));
        //ctrButton.setDisable(false);
        System.out.println("[Series] CTR... done");
        importProgress.countDown();
    }


    private void updateTotalCostSeries(int resolution) {
        in.setTotalCostSeries(ViewDataParser.getTotalCostSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getTotalCostSeries()));
        //totalCostButton.setDisable(false);
        System.out.println("[Series] total cost... done");
        importProgress.countDown();
    }

    public void newChartButtonAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("newchart.fxml"));
            Parent chartForm = fxmlLoader.load();
            ChartFactory chartController = fxmlLoader.getController();

            chartController.setChartPane(dashboardArea);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(chartForm));
            stage.sizeToScene();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}