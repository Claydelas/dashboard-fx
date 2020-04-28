package group18.dashboard.controllers;

import group18.dashboard.App;
import group18.dashboard.database.tables.Campaign;
import group18.dashboard.database.tables.records.CampaignRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jooq.impl.DSL;

import static group18.dashboard.App.*;
import static group18.dashboard.database.tables.Campaign.*;
import static group18.dashboard.database.tables.Click.CLICK;

public class DashboardController {

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
    public TabPane tabs;
    public FlowPane dashboardArea;
    ObservableList<String> campaigns;

    @FXML
    public void initialize() {
        dashboardArea.prefWrapLengthProperty().bind(dashboardArea.widthProperty());
        loadTabs();
        campaigns = FXCollections.observableArrayList(
                query.select(CAMPAIGN.NAME).from(CAMPAIGN).where(CAMPAIGN.PARSED).fetch(CAMPAIGN.NAME));
    }

    //for each complete campaign generate a tab
    private void loadTabs() {
        query.selectFrom(CAMPAIGN).where(CAMPAIGN.PARSED).fetch().forEach(this::loadTab);
    }

    //generates a tab and its contents and adds it to the TabPane
    public void loadTab(CampaignRecord campaignRecord){
        Tab tab = new Tab(campaignRecord.getName());
        GridPane content = new GridPane();
        content.setPadding(new Insets(10,10,10,10));
        content.setVgap(10);
        content.setHgap(10);
        content.addColumn(0
                , new Label("Impressions")
                , new Label("Clicks")
                , new Label("Uniques")
                , new Label("Bounces")
                , new Label("Conversions")
                , new Label("Total Cost")
                , new Label("Click-through-rate")
                , new Label("Cost-per-acquisition")
                , new Label("Cost-per-click")
                , new Label("Cost-per-mille")
                , new Label("Bounce Rate"));
        content.addColumn(1
                , new Label(String.format("%,d", campaignRecord.getImpressions()))
                , new Label(String.format("%,d", campaignRecord.getClicks()))
                , new Label(String.format("%,d", campaignRecord.getUniques()))
                , new Label(String.format("%,d", campaignRecord.getBounces()))
                , new Label(String.format("%,d", campaignRecord.getConversions()))
                , new Label(String.format("\u00A3%.2f", campaignRecord.getTotalCost()))
                , new Label(String.format("%.2f%%", campaignRecord.getCtr()))
                , new Label(String.format("\u00A3%.2f", campaignRecord.getCpa()))
                , new Label(String.format("\u00A3%.2f", campaignRecord.getCpc()))
                , new Label(String.format("\u00A3%.5f", campaignRecord.getCpm()))
                , new Label(String.format("%.2f%%",campaignRecord.getBounceRate()*100)));
        tab.setClosable(false);
        tab.setContent(content);
        tabs.getTabs().add(tab);
    }


    @FXML
    public void importCampaignButtonAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("import.fxml"));
            Parent importForm = fxmlLoader.load();
            ImportController importController = fxmlLoader.getController();
            importController.setParentController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(importForm));
            stage.setTitle("Import Campaign Data");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newChartButtonAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("newchart.fxml"));
            Parent chartForm = fxmlLoader.load();
            ChartFactory chartController = fxmlLoader.getController();

            chartController.setChartPane(dashboardArea);
            chartController.setCampaigns(campaigns);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(chartForm));
            stage.setTitle("New Chart");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addCampaign(String name){
        campaigns.add(name);
    }
}