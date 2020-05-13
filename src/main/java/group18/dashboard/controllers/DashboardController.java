package group18.dashboard.controllers;

import com.jfoenix.controls.JFXButton;
import group18.dashboard.App;
import group18.dashboard.database.tables.records.CampaignRecord;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;

import static group18.dashboard.App.query;
import static group18.dashboard.database.tables.Campaign.CAMPAIGN;

public class DashboardController {

    public BorderPane chartPane;
    public TabPane campaignTabs;
    public FlowPane currentDashboardArea;
    public ScrollPane scrollPane;
    public BorderPane appView;
    public Slider uiScalingSlider;
    public TabPane chartAreaTabs;
    public Tab addTab;
    public Tab firstTab;
    public FlowPane firstDashboardArea;
    ObservableList<String> campaignNames;

    @FXML
    public void initialize() {
        scrollPane.getContent().setOnMousePressed(Event::consume);
        firstDashboardArea.prefWrapLengthProperty().bind(firstDashboardArea.widthProperty());

        chartPane.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpx;", uiScalingSlider.valueProperty()));

        Result<CampaignRecord> campaigns
                = query.fetch(CAMPAIGN, CAMPAIGN.UID.eq(LoginController.getLoggedUserID()).and(CAMPAIGN.PARSED));

        loadTabs(campaigns);
        campaignNames = FXCollections.observableArrayList(campaigns.getValues(CAMPAIGN.NAME));

        currentDashboardArea = firstDashboardArea;
        setupFirstCampaignButton(currentDashboardArea);
        firstTab.setOnSelectionChanged(e -> {
            if (((Tab) e.getSource()).isSelected()) {
                currentDashboardArea = firstDashboardArea;
            }
        });
    }

    void setupFirstCampaignButton(FlowPane dashboardArea) {
        final JFXButton initChartButton = new JFXButton(" You can insert more charts from \"New\"");
        initChartButton.setStyle("-fx-font-size:26");
        ImageView image = new ImageView(App.class.getResource("icons/baseline_add_black.png").toString());
        image.setFitWidth(45);
        image.setFitHeight(45);
        image.setPreserveRatio(true);
        initChartButton.setGraphic(image);
        initChartButton.setOnMouseClicked(e -> newChartButtonAction());
        dashboardArea.getChildren().add(initChartButton);
        dashboardArea.getChildren().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                Platform.runLater(() -> {
                    dashboardArea.requestFocus();
                    dashboardArea.getChildren().remove(initChartButton);
                });
                observable.removeListener(this);
            }
        });
    }

    //for each complete campaign generate a tab
    private void loadTabs(Result<CampaignRecord> campaigns) {
        campaigns.forEach(this::loadTab);
    }

    //generates a tab and its contents and adds it to the TabPane
    public void loadTab(CampaignRecord campaignRecord) {
        Tab tab = campaignTabs.getTabs().parallelStream()
                .filter(x -> x.getText().equals(campaignRecord.getName()))
                .findAny()
                .orElseGet(() -> new Tab(campaignRecord.getName()));

        BorderPane content = new BorderPane();
        GridPane campaignInfo = new GridPane();
        campaignInfo.setPadding(new Insets(10, 10, 10, 10));
        campaignInfo.setVgap(10);
        campaignInfo.setHgap(10);
        campaignInfo.addColumn(0
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
        campaignInfo.addColumn(1
                , new Label(String.format("%,d", campaignRecord.getImpressions()))
                , new Label(String.format("%,d", campaignRecord.getClicks()))
                , new Label(String.format("%,d", campaignRecord.getUniques()))
                , new Label(String.format("%,d", campaignRecord.getBounces()))
                , new Label(String.format("%,d", campaignRecord.getConversions()))
                , new Label(String.format("\u00A3%.2f", campaignRecord.getTotalCost() / 100))
                , new Label(String.format("%.2f%%", campaignRecord.getCtr() * 100))
                , new Label(String.format("\u00A3%.3f", campaignRecord.getCpa() / 100))
                , new Label(String.format("\u00A3%.3f", campaignRecord.getCpc() / 100))
                , new Label(String.format("\u00A3%.3f", campaignRecord.getCpm() / 100))
                , new Label(String.format("%.2f%%", campaignRecord.getBounceRate() * 100)));
        content.setTop(campaignInfo);

        VBox bounceInfo = new VBox();
        bounceInfo.setPadding(new Insets(7, 7, 7, 7));

        bounceInfo.getChildren().add(new Label("Current bounce definition:"));

        final Label pagesLabel = new Label(
                "- If less than " + campaignRecord.getMinPages() +
                        " website pages were visited");
        pagesLabel.setPadding(new Insets(2, 0, 2, 9));

        final Label timeLabel = new Label(
                "- If less than " + campaignRecord.getMinTime() +
                        " minutes were spent on the website");
        timeLabel.setPadding(new Insets(2, 0, 2, 9));


        if (campaignRecord.getMinPagesEnabled()) {
            bounceInfo.getChildren().add(pagesLabel);
        }
        if (campaignRecord.getMinTimeEnabled()) {
            bounceInfo.getChildren().add(timeLabel);
        }

        if (!campaignRecord.getMinTimeEnabled() && !campaignRecord.getMinPagesEnabled()) {
            bounceInfo.getChildren().add(new Label("No bounce definition currently set."));
        }

        Button changeBounce = new Button("Update bounce definition");
        changeBounce.setOnAction(e -> updateBounceDefinitionButtonAction());
        bounceInfo.getChildren().add(changeBounce);

        content.setBottom(bounceInfo);

        tab.setClosable(false);
        tab.setContent(content);
        campaignTabs.getTabs().add(tab);
    }

    @FXML
    public void importCampaignButtonAction() { // TODO give currently selected campaign as default
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("import.fxml"));
            Parent importForm = fxmlLoader.load();
            ImportController importController = fxmlLoader.getController();
            importController.setParentController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(importForm));
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/app.png")));
            stage.setTitle("Import Campaign Data");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void updateBounceDefinitionButtonAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("bounces.fxml"));
            Parent bounceDefinitionForm = fxmlLoader.load();
            BounceDefinitionController bounceDefinitionController = fxmlLoader.getController();
            bounceDefinitionController.campaignName = getCampaignName();
            bounceDefinitionController.setParentController(this);
            bounceDefinitionController.setCurrentBounceValues();


            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(bounceDefinitionForm));
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/app.png")));
            stage.setTitle("Update bounce definition");
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

            chartController.setChartPane(currentDashboardArea);
            chartController.setCampaigns(campaignNames);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(chartForm));
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/app.png")));
            stage.setTitle("New Chart");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCampaignName() {
        return campaignTabs.getSelectionModel().getSelectedItem().getText();
    }

    public void addCampaign(String name) {
        campaignNames.add(name);
    }

    @FXML
    public void themeButtonAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("oops..");
        alert.setHeaderText(null);
        alert.setContentText("This feature has not been implemented yet!");
        alert.showAndWait();
    }

    public void addButtonSelection(Event event) {
        if (((Tab) event.getSource()).isSelected()) { // True when we select the add tab
            final String prevTabText = chartAreaTabs.getTabs().get(chartAreaTabs.getTabs().size() - 2).getText();
            Tab tab = new Tab("Graphs " + (Integer.parseInt(Character.toString(prevTabText.charAt(prevTabText.length() - 1))) + 1));

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);

            FlowPane flowPane = new FlowPane();
            flowPane.setAlignment(Pos.CENTER);
            flowPane.setColumnHalignment(HPos.CENTER);
            flowPane.setHgap(5);
            flowPane.setVgap(5);
            scrollPane.setContent(flowPane);
            tab.setContent(flowPane);

            tab.setOnSelectionChanged(e -> {
                if (((Tab) e.getSource()).isSelected()) {
                    currentDashboardArea = flowPane;
                }
            });

            setupFirstCampaignButton(flowPane);

            chartAreaTabs.getTabs().add(chartAreaTabs.getTabs().size() - 1, tab);
            chartAreaTabs.getSelectionModel().selectPrevious();

        }
    }
}