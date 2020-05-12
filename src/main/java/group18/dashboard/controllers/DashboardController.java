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
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jooq.Result;

import static group18.dashboard.App.query;
import static group18.dashboard.database.tables.Campaign.CAMPAIGN;

public class DashboardController {

    public BorderPane chartPane;
    public TabPane tabs;
    public FlowPane dashboardArea;
    public ScrollPane scrollPane;
    public BorderPane appView;
    public Slider uiScalingSlider;
    ObservableList<String> campaignNames;

    @FXML
    public void initialize() {
        scrollPane.getContent().setOnMousePressed(Event::consume);
        dashboardArea.prefWrapLengthProperty().bind(dashboardArea.widthProperty());

        chartPane.styleProperty().bind(Bindings.format("-fx-font-size: %.2fpx;", uiScalingSlider.valueProperty()));

        Result<CampaignRecord> campaigns
                = query.fetch(CAMPAIGN, CAMPAIGN.UID.eq(LoginController.getLoggedUserID()).and(CAMPAIGN.PARSED));

        loadTabs(campaigns);
        campaignNames = FXCollections.observableArrayList(campaigns.getValues(CAMPAIGN.NAME));

        setupFirstCampaignButton();
    }

    void setupFirstCampaignButton() {
        final JFXButton initChartButton = new JFXButton("You can insert more charts from \"New\"");
        initChartButton.setStyle("-fx-font-size:30");
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
        Tab tab = new Tab(campaignRecord.getName());
        GridPane content = new GridPane();
        content.setPadding(new Insets(10, 10, 10, 10));
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
                , new Label(String.format("\u00A3%.2f", campaignRecord.getTotalCost() / 100))
                , new Label(String.format("%.2f%%", campaignRecord.getCtr() * 100))
                , new Label(String.format("\u00A3%.3f", campaignRecord.getCpa() / 100))
                , new Label(String.format("\u00A3%.3f", campaignRecord.getCpc() / 100))
                , new Label(String.format("\u00A3%.3f", campaignRecord.getCpm() / 100))
                , new Label(String.format("%.2f%%", campaignRecord.getBounceRate() * 100)));
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
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/app.png")));
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
}