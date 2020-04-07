package group18.dashboard.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
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
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static group18.dashboard.model.Campaign.distinctByKey;

public class DashboardController {

    public NumberAxis yAxis;
    public LineChart<String, Number> mainChart;
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
    public JFXRadioButton impressionsButton;
    public JFXRadioButton clicksButton;
    public JFXRadioButton uniquesButton;
    public JFXRadioButton bouncesButton;
    public JFXRadioButton conversionsButton;
    public JFXRadioButton totalCostButton;
    public JFXRadioButton ctrButton;
    public JFXRadioButton cpaButton;
    public JFXRadioButton cpcButton;
    public JFXRadioButton cpmButton;
    public JFXRadioButton bounceRateButton;
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

        bindMetrics(in);
        bindChartMetrics(in);
    }

    @FXML
    public void importCampaignButtonAction() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Campaign Folder");
        File dir = directoryChooser.showDialog(tabs.getScene().getWindow());

        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && !(Arrays.stream(files).allMatch(file -> file.getName().equals("click_log.csv")
                    || file.getName().equals("impression_log.csv") || file.getName().equals("server_log.csv")))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing files");
                alert.setHeaderText(null);
                alert.setContentText("Couldn't find all campaign files in the selected directory!\nPlease try again.");
                alert.showAndWait();
                return;
            }
        }
        if (dir == null) return;

        System.out.println("--Parsing Campaign--");
        ExecutorService executor = Executors.newWorkStealingPool();
        CountDownLatch latch = new CountDownLatch(3);
        importProgress = new CountDownLatch(22);

        executor.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();
                in.updateClicks(dir.getAbsolutePath());
                long endTime = System.currentTimeMillis();
                System.out.println("Parsed " + in.getClicks().size() + " clicks in " + (endTime - startTime) + "ms");
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.execute(() ->
        {
            try {
                long startTime = System.currentTimeMillis();
                in.updateImpressions(dir.getAbsolutePath());
                long endTime = System.currentTimeMillis();
                System.out.println("Parsed " + in.getImpressions().size() + " impressions in " + (endTime - startTime) + "ms");
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();
                in.updateInteractions(dir.getAbsolutePath());
                long endTime = System.currentTimeMillis();
                System.out.println("Parsed " + in.getInteractions().size() + " interactions in " + (endTime - startTime) + "ms");
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
        mainView.getChildren().remove(initView);
        loadingProgress.setVisible(true);
        executor.execute(() -> {
            try {
                importProgress.await();
                Platform.runLater(() -> {
                    mainView.getChildren().remove(loadingProgress);
                    chartPane.setVisible(true);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
    }

    private void bindMetrics(Campaign campaign) {
        impressions.textProperty().bind(campaign.impressionCountProperty().asString("%,d"));
        clicks.textProperty().bind(campaign.clickCountProperty().asString("%,d"));
        uniques.textProperty().bind(campaign.uniquesProperty().asString("%,d"));
        bounces.textProperty().bind(campaign.bouncesProperty().asString("%,d"));
        conversions.textProperty().bind(campaign.conversionsProperty().asString("%,d"));
        totalCost.textProperty().bind(campaign.totalCostProperty().asString("\u00A3%.2f"));
        ctr.textProperty().bind(campaign.ctrProperty().asString("%.2f%%"));
        cpa.textProperty().bind(campaign.cpaProperty().asString("\u00A3%.2f"));
        cpc.textProperty().bind(campaign.cpcProperty().asString("\u00A3%.2f"));
        cpm.textProperty().bind(campaign.cpmProperty().asString("\u00A3%.2f"));
        bounceRate.textProperty().bind(campaign.bounceRateProperty().asString("%.2f%%"));
    }

    private void bindChartMetrics(Campaign campaign) {
        mainChart.dataProperty().bind(series);

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

    private void updateChartMetrics(ExecutorService executor, int resolution) {
        executor.execute(() -> updateBounceRateSeries(resolution));
        executor.execute(() -> updateCPASeries(resolution));
        executor.execute(() -> updateCPCSeries(resolution));
        executor.execute(() -> updateCPMSeries(resolution));
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
        importProgress.countDown();
    }

    private void updateBouncesSeries(int resolution) {
        in.setBouncesSeries(ViewDataParser.getCumulativeTimeSeries("Bounces", resolution,
                in.getInteractions().parallelStream()
                        .filter(interaction -> !interaction.isConversion()).map(Interaction::getEntryDate).collect(Collectors.toList())));
        bouncesButton.setDisable(false);
        System.out.println("[Series] bounces... done");
        importProgress.countDown();
    }

    private void updateClickCountSeries(int resolution) {
        in.setClickCountSeries(ViewDataParser.getCumulativeTimeSeries("Clicks", resolution,
                in.getClicks().parallelStream().map(Click::getDate).collect(Collectors.toList())));
        clicksButton.setDisable(false);
        System.out.println("[Series] clicks... done");
        importProgress.countDown();
    }

    private void updateConversionsSeries(int resolution) {
        in.setConversionSeries(ViewDataParser.getCumulativeTimeSeries("Conversions", resolution,
                in.getInteractions().parallelStream()
                        .filter(Interaction::isConversion).map(Interaction::getEntryDate).collect(Collectors.toList())));
        conversionsButton.setDisable(false);
        System.out.println("[Series] conversions... done");
        importProgress.countDown();
    }

    private void updateCPASeries(int resolution) {
        in.setCPASeries(ViewDataParser.getCPATimeSeries(resolution, in.getImpressions(), in.getClicks(), in.getInteractions()));
        //Platform.runLater(()->seriesList.add(in.getCPASeries()));
        cpaButton.setDisable(false);
        System.out.println("[Series] CPA... done");
        importProgress.countDown();
    }

    private void updateCPCSeries(int resolution) {
        in.setCPCSeries(ViewDataParser.getCPCTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCPCSeries()));
        cpcButton.setDisable(false);
        System.out.println("[Series] CPC... done");
        importProgress.countDown();
    }

    private void updateCPMSeries(int resolution) {
        in.setCPMSeries(ViewDataParser.getCPMTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCPMSeries()));
        cpmButton.setDisable(false);
        System.out.println("[Series] CPM... done");
        importProgress.countDown();
    }

    private void updateCTRSeries(int resolution) {
        in.setCtrSeries(ViewDataParser.getCTRTimeSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getCtrSeries()));
        ctrButton.setDisable(false);
        System.out.println("[Series] CTR... done");
        importProgress.countDown();
    }

    private void updateImpressionsSeries(int resolution) {
        in.setImpressionSeries(ViewDataParser.getCumulativeTimeSeries("Impressions", resolution,
                in.getImpressions().parallelStream().map(Impression::getDate).collect(Collectors.toList())));
        impressionsButton.setDisable(false);
        System.out.println("[Series] impressions... done");
        importProgress.countDown();
    }

    private void updateTotalCostSeries(int resolution) {
        in.setTotalCostSeries(ViewDataParser.getTotalCostSeries(resolution, in.getImpressions(), in.getClicks()));
        //Platform.runLater(()->seriesList.add(in.getTotalCostSeries()));
        totalCostButton.setDisable(false);
        System.out.println("[Series] total cost... done");
        importProgress.countDown();
    }

    private void updateUniquesSeries(int resolution) {
        in.setUniquesSeries(ViewDataParser.getCumulativeTimeSeries("Uniques", resolution,
                in.getClicks().parallelStream().filter(distinctByKey(Click::getDate)).map(Click::getDate).collect(Collectors.toList())));
        uniquesButton.setDisable(false);
        System.out.println("[Series] uniques... done");
        importProgress.countDown();
    }

    public void newChartButtonAction() {
        XYChart.Series<String, Number> series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data("MO", new Random().nextInt()));
        series1.getData().add(new XYChart.Data("TU", new Random().nextInt()));

        final XYChart<String, Number> bp = new LineChart<>(new CategoryAxis(), new NumberAxis());
        bp.getData().add(series1);

        makeDraggable(bp);
        dashboardArea.getChildren().add(bp);
    }

    private void makeDraggable(Node node) {
        node.setOnDragDetected(event -> {
            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent clipboard = new ClipboardContent();
            final int nodeIndex = node.getParent().getChildrenUnmodifiable()
                    .indexOf(node);
            clipboard.putString(Integer.toString(nodeIndex));
            db.setContent(clipboard);
            event.consume();
        });
        node.setOnDragOver(event -> {
            boolean accept = true;
            final Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                int incomingIndex = Integer.parseInt(dragboard.getString());
                int myIndex = node.getParent().getChildrenUnmodifiable()
                        .indexOf(node);
                if (incomingIndex == myIndex) {
                    accept = false;
                }
            } else {
                accept = false;
            }
            if (accept) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });
        node.setOnDragDropped(event -> {
            boolean success = false;
            final Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                int incomingIndex = Integer.parseInt(dragboard.getString());
                final Pane parent = (Pane) node.getParent();
                final ObservableList<Node> children = parent.getChildren();
                int myIndex = children.indexOf(node);
                final int laterIndex = Math.max(incomingIndex, myIndex);
                Node removedLater = children.remove(laterIndex);
                final int earlierIndex = Math.min(incomingIndex, myIndex);
                Node removedEarlier = children.remove(earlierIndex);
                children.add(earlierIndex, removedLater);
                children.add(laterIndex, removedEarlier);
                success = true;
            }
            event.setDropCompleted(success);
        });
    }
}