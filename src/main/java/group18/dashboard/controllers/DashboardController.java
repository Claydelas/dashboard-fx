package group18.dashboard.controllers;

import group18.dashboard.App;
import group18.dashboard.model.Campaign;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    public FlowPane dashboardArea;

    @FXML
    public void initialize() {

        Button debug = new Button("debug");
        mainView.getChildren().add(debug);
        StackPane.setAlignment(debug, Pos.TOP_RIGHT);
        debug.setOnMouseClicked(e -> {
            mainView.getChildren().remove(initView);
            mainView.getChildren().remove(debug);
            chartPane.setVisible(true);
        });

        dashboardArea.prefWrapLengthProperty().bind(dashboardArea.widthProperty());
    }

    @FXML
    public void importCampaignButtonAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("import.fxml"));
            Parent importForm = fxmlLoader.load();
            //ImportController importController = fxmlLoader.getController();

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