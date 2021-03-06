package group18.dashboard.controllers;

import com.jfoenix.controls.JFXComboBox;
import group18.dashboard.App;
import group18.dashboard.database.tables.records.CampaignRecord;
import group18.dashboard.util.DB;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jooq.*;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.DayToSecond;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static group18.dashboard.App.query;
import static group18.dashboard.database.tables.Campaign.CAMPAIGN;
import static group18.dashboard.database.tables.Click.CLICK;
import static group18.dashboard.database.tables.Impression.IMPRESSION;
import static group18.dashboard.database.tables.Interaction.INTERACTION;

public class ImportController {

    public TextField impressionLogPath;
    public Button browseImpressionLog;
    public TextField clickLogPath;
    public Button browseClickLog;
    public TextField interactionLogPath;
    public Button browseInteractionLog;
    public TextField folderPath;
    public Button browseFolder;
    public VBox importForm;
    public JFXComboBox<String> source;
    public StackPane panes;
    public TextField campaignNameField;
    public ProgressBar filesImportProgress;
    public ProgressBar folderImportProgress;
    public Button folderButton;
    public Button filesButton;
    ExecutorService executor;
    File folder;
    private DashboardController parentController;

    boolean isValidFolder(String path) {
        Path folder = Paths.get(path);
        if (!path.isBlank() && Files.isDirectory(folder)) {
            if (Files.isReadable(Paths.get(path, "click_log.csv"))
                    && Files.isReadable(Paths.get(path, "impression_log.csv"))
                    && Files.isReadable(Paths.get(path, "server_log.csv"))) {
                return true;
            }
        }
        App.alert("Missing files", "Couldn't find all campaign files in the selected directory!\nPlease try again.");
        return false;
    }

    // returns the text of the name textfield or generates a name (Campaign ID) if the textfield is empty
    public String generateCampaignName() {
        String name = campaignNameField.getText();
        return name.isBlank() || nameTaken(name) ? "Campaign " + (query.selectCount().from(CAMPAIGN).fetchOneInto(int.class) + 1) : name;
    }

    // inserts a new entry to the Campaign table
    private int insertCampaign() {
        String campaignName = generateCampaignName();
        int campaignID = query
                .insertInto(CAMPAIGN, CAMPAIGN.NAME, CAMPAIGN.UID)
                .values(campaignName, LoginController.getLoggedUserID())
                .returningResult(CAMPAIGN.CID)
                .fetchOne().value1();
        System.out.println("Added new campaign \"" + campaignName + "\" with id: " + campaignID);
        return campaignID;
    }

    public void importFolder() {
        String folderDir = folderPath.getText();
        if (isValidFolder(folderDir)) {
            folderButton.setDisable(true);
            folderImportProgress.setVisible(true);
            int campaignID = insertCampaign();
            CountDownLatch latch = new CountDownLatch(3);
            Arrays.stream(Objects.requireNonNull(Paths.get(folderDir).toFile().listFiles())).forEach(file -> {
                try {
                    String name = file.getName();
                    if (name.equals("impression_log.csv"))
                        executor.execute(() -> {
                            parseImpressions(file.getAbsolutePath(), campaignID);
                            DB.commit();
                            latch.countDown();
                        });
                    if (name.equals("click_log.csv"))
                        executor.execute(() -> {
                            parseClicks(file.getAbsolutePath(), campaignID);
                            DB.commit();
                            latch.countDown();
                        });
                    if (name.equals("server_log.csv"))
                        executor.execute(() -> {
                            parseInteractions(file.getAbsolutePath(), campaignID);
                            DB.commit();
                            latch.countDown();
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            onParsingFinished(campaignID, latch);
            //TODO update progress indicator
        }

    }

    private void loadDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("dashboard.fxml"));
            Parent app = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(app, 1024, 600));
            stage.setTitle("Ad Auction Dashboard alpha");
            stage.setMinHeight(500);
            stage.setMinWidth(800);
            stage.show();
            exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importFiles() {
        String impressions = impressionLogPath.getText();
        String clicks = clickLogPath.getText();
        String interactions = interactionLogPath.getText();

        if (impressions.isBlank() || !Files.isReadable(Paths.get(impressions))) {
            App.alert("Missing file", "Impression log wasn't found at the selected location.\nPlease try again.");
            return;
        }
        if (clicks.isBlank() || !Files.isReadable(Paths.get(clicks))) {
            App.alert("Missing file", "Click log wasn't found at the selected location.\nPlease try again.");
            return;
        }
        if (interactions.isBlank() || !Files.isReadable(Paths.get(interactions))) {
            App.alert("Missing file", "Server log wasn't found at the selected location.\nPlease try again.");
            return;
        }

        filesButton.setDisable(true);
        filesImportProgress.setVisible(true);
        int campaignID = insertCampaign();
        CountDownLatch latch = new CountDownLatch(3);

        executor.execute(() -> {
            parseImpressions(impressions, campaignID);
            DB.commit();
            latch.countDown();
        });

        executor.execute(() -> {
            parseClicks(clicks, campaignID);
            DB.commit();
            latch.countDown();
        });

        executor.execute(() -> {
            parseInteractions(interactions, campaignID);
            DB.commit();
            latch.countDown();
        });
        onParsingFinished(campaignID, latch);
        //TODO update progress indicator
    }

    private void onParsingFinished(int campaignID, CountDownLatch latch) {
        executor.execute(() -> {
            try {
                latch.await();
                calculateMetrics(campaignID);
                if (parentController == null) Platform.runLater(this::loadDashboard);
                else Platform.runLater(this::exit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void exit() {
        executor.shutdown();
        Stage stage = (Stage) importForm.getScene().getWindow();
        stage.close();
    }

    public void selectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
        directoryChooser.setTitle("Select Campaign Folder");
        folder = directoryChooser.showDialog(importForm.getScene().getWindow());
        if (folder == null) return;
        folderPath.setText(folder.getAbsolutePath());
    }

    public void selectImpressionLog() {
        selectLog("Impression Log", impressionLogPath);
    }

    public void selectClickLog() {
        selectLog("Click Log", clickLogPath);
    }

    public void selectInteractionLog() {
        selectLog("Server Log", interactionLogPath);
    }

    public void selectLog(String fileType, TextField pathField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
        fileChooser.setTitle("Select " + fileType);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(fileType, "*.csv"));
        File log = fileChooser.showOpenDialog(importForm.getScene().getWindow());
        if (log == null) return;
        pathField.setText(log.getAbsolutePath());
    }

    public void parseImpressions(String path, int campaignID) {
        String sql = "insert into IMPRESSION (DATE, USER, GENDER, AGE, INCOME, CONTEXT, COST, CID) " +
                "select \"DATE\",\"ID\",\"GENDER\",\"AGE\",\"INCOME\",\"CONTEXT\",\"Impression Cost\", " + campaignID +
                " from CSVREAD('" + path + "', null);";

        long startTime = System.currentTimeMillis();
        try {
            DB.connection().prepareStatement(sql).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Done parsing " + path + " in " + (endTime - startTime) + "ms");

    }

    public void parseClicks(String path, int campaignID) {
        String sql = "insert into CLICK (DATE, USER, COST, CID) " +
                "select \"DATE\", \"ID\", \"Click Cost\", " + campaignID +
                " from CSVREAD('" + path + "', null);";

        long startTime = System.currentTimeMillis();
        try {
            DB.connection().prepareStatement(sql).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Done parsing " + path + " in " + (endTime - startTime) + "ms");

    }

    public void parseInteractions(String path, int campaignID) {
        String sql = "insert into INTERACTION (ENTRY_DATE, USER, EXIT_DATE, VIEWS, CONVERSION, CID) " +
                "select \"Entry Date\", \"ID\", " +
                "(case when \"Exit Date\" = 'n/a' then null else \"Exit Date\" end), " +
                "\"Pages Viewed\", " +
                "(case when \"CONVERSION\" = 'Yes' then true else false end), " +
                campaignID +
                " from CSVREAD('" + path + "', null);";

        long startTime = System.currentTimeMillis();
        try {
            DB.connection().prepareStatement(sql).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Done parsing " + path + " in " + (endTime - startTime) + "ms");
    }

    @FXML
    public void initialize() {
        query = DSL.using(DB.connection(), SQLDialect.H2);
        executor = Executors.newWorkStealingPool();

        panes.getChildren().forEach(node -> node.managedProperty().bind(node.visibleProperty()));
        //changes view depending on combobox selection
        source.getSelectionModel().selectedIndexProperty().addListener((p, o, newval) -> {
            panes.getChildren().forEach(node -> {
                node.setVisible(false);
            });
            panes.getChildren().get(newval.intValue()).setVisible(true);
            Platform.runLater(() -> importForm.getScene().getWindow().sizeToScene());
        });
        //initial selection
        source.getSelectionModel().selectFirst();

    }

    void calculateMetrics(int campaignID) {

        System.out.println("Calculating metrics.");

        int clicks = query.selectCount().from(CLICK).where(CLICK.CID.eq(campaignID)).fetchOneInto(int.class);

        int impressions = query.selectCount().from(IMPRESSION).where(IMPRESSION.CID.eq(campaignID)).fetchOneInto(int.class);

        int bounces = clicks - getSuccessfulInteractions(campaignID);

        int uniques = query.select(DSL.countDistinct(CLICK.USER)).from(CLICK).where(CLICK.CID.eq(campaignID)).fetchOneInto(int.class);

        int conversions = query.selectCount().from(INTERACTION).where(INTERACTION.CID.eq(campaignID).and(INTERACTION.CONVERSION)).fetchOneInto(int.class);

        double impressionCostSum = query.select(DSL.sum(IMPRESSION.COST)).from(IMPRESSION).where(IMPRESSION.CID.eq(campaignID)).fetchOneInto(double.class);

        double clickCostSum = query.select(DSL.sum(CLICK.COST)).from(CLICK).where(CLICK.CID.eq(campaignID)).fetchOneInto(double.class);

        double totalCost = impressionCostSum + clickCostSum;

        query.update(CAMPAIGN).set(CAMPAIGN.IMPRESSIONS, impressions).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.CLICKS, clicks).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.UNIQUES, uniques).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.BOUNCES, bounces).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.CONVERSIONS, conversions).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.CTR, (double) clicks / impressions).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.CPA, totalCost / conversions).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.CPC, totalCost / clicks).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.CPM, ((totalCost / impressions) * 1000)).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.BOUNCE_RATE, (double) bounces / clicks).where(CAMPAIGN.CID.eq(campaignID)).execute();
        query.update(CAMPAIGN).set(CAMPAIGN.TOTAL_COST, totalCost).where(CAMPAIGN.CID.eq(campaignID)).execute();

        CampaignRecord result = query.update(CAMPAIGN).set(CAMPAIGN.PARSED, true).where(CAMPAIGN.CID.eq(campaignID)).returning().fetchOne();
        DB.commit();
        System.out.println("Metrics calculated successfully.");
        if (parentController != null) {
            Platform.runLater(() -> {
                parentController.loadTab(result);
                parentController.addCampaign(result.getName());
            });
        }
    }

    public static int getSuccessfulInteractions(int campaignID) {
        Record4<Boolean, Boolean, Double, Integer> q = query.select(
                CAMPAIGN.MIN_TIME_ENABLED,
                CAMPAIGN.MIN_PAGES_ENABLED,
                CAMPAIGN.MIN_TIME,
                CAMPAIGN.MIN_PAGES)
                .from(CAMPAIGN).where(CAMPAIGN.CID.eq(campaignID)).fetchOne();

        boolean isMinTimeEnabled = q.value1();
        boolean isMinPagesEnabled = q.value2();
        double minTime = q.value3();
        int minPages = q.value4();

        return query.selectCount().from(INTERACTION)
                .where(INTERACTION.CID.eq(campaignID))
                .and(isMinTimeEnabled ? INTERACTION.EXIT_DATE.isNotNull() : DSL.trueCondition())
                .and(isMinTimeEnabled ? DSL.condition("datediff('ms', ENTRY_DATE, EXIT_DATE) >= " + (minTime * 60 * 1000)) : DSL.trueCondition())
                .and(isMinPagesEnabled ? INTERACTION.VIEWS.ge(minPages) : DSL.trueCondition())
                .fetchOne().value1();
    }

    public void setParentController(DashboardController dashboardController) {
        this.parentController = dashboardController;
    }

    public boolean nameTaken(String name) {
        return query.fetchExists(CAMPAIGN, CAMPAIGN.NAME.equalIgnoreCase(name));
    }
}
