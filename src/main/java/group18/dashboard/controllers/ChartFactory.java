package group18.dashboard.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.validation.RequiredFieldValidator;
import group18.dashboard.ViewDataParser;
import group18.dashboard.model.Campaign;
import group18.dashboard.model.Impression;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ChartFactory {

    public Button cancel;
    public CheckBox filterLow;
    public CheckBox filterMedium;
    public CheckBox filterHigh;
    public CheckBox filterMale;
    public CheckBox filterFemale;
    public CheckBox filterBelow25;
    public CheckBox filter25to34;
    public CheckBox filter35to44;
    public CheckBox filter45to54;
    public CheckBox filterAbove54;
    public CheckBox filterNews;
    public CheckBox filterShopping;
    public CheckBox filterSocialMedia;
    public CheckBox filterBlog;
    public CheckBox filterHobbies;
    public CheckBox filterTravel;
    public JFXComboBox<String> metricComboBox;
    public JFXComboBox<String> campaignComboBox;
    public JFXComboBox<String> chartTypeComboBox;
    public Button addChart;
    private FlowPane dashboardArea;

    @FXML
    public void initialize() {
        metricComboBox.setValidators(new RequiredFieldValidator());
        campaignComboBox.setValidators(new RequiredFieldValidator());
        chartTypeComboBox.setValidators(new RequiredFieldValidator());

    }

    public void setChartPane(FlowPane p) {
        this.dashboardArea = p;
    }

    public void addChartAction() {
        // validates all required fields on entry
        if (!chartTypeComboBox.validate() /*|| !campaignComboBox.validate()*/ || !metricComboBox.validate()) return;

        ExecutorService executor = Executors.newWorkStealingPool();

        executor.execute(() -> {
            Campaign temp = new Campaign();
            try {
                temp.updateImpressions("D:\\Projects\\Java\\2_week_campaign");
            } catch (Exception e) {
                e.printStackTrace();
            }
            final XYChart<String, Number> chart;

            switch (chartTypeComboBox.getSelectionModel().getSelectedItem()) {
                case "Line Chart":
                    chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
                    System.out.println("Debug : LINE CHART");
                    break;
                default:
                    chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
            }


            switch (metricComboBox.getSelectionModel().getSelectedItem()) {
                case "Impressions":
                    chart.getData().add(ViewDataParser.getCumulativeTimeSeries(
                            "Impressions",
                            5,
                            temp.getImpressions().parallelStream().map(Impression::getDate).collect(Collectors.toList())));
                    break;
                case "Clicks":
                    System.out.println("hello");
                    break;
            }

            makeDraggable(chart);
            Platform.runLater(() -> dashboardArea.getChildren().add(chart));
        });
        executor.shutdown();
        //implicit closing of stage after a chart is added
        cancelAction();
    }

    public void cancelAction() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
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
