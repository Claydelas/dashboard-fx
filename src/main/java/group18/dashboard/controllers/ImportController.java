package group18.dashboard.controllers;

import com.jfoenix.controls.JFXComboBox;
import group18.dashboard.util.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static group18.dashboard.database.tables.Campaign.CAMPAIGN;
import static group18.dashboard.util.Parsing.*;

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
    public TextField zipPath;
    public Button browseZip;
    public TextField campaignNameField;
    DSLContext query;
    ExecutorService executor;
    File folder;
    private int campaignID;

    boolean isValidFolder(File dir) {
        if (dir == null) return false;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && Arrays.stream(files).noneMatch(file -> file.getName().equals("click_log.csv")
                    || file.getName().equals("impression_log.csv") || file.getName().equals("server_log.csv"))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing files");
                alert.setHeaderText(null);
                alert.setContentText("Couldn't find all campaign files in the selected directory!\nPlease try again.");
                alert.showAndWait();
                return false;
            }
            return true;
        }
        return false;
    }

    // returns the text of the name textfield or generates a name (Campaign ID) if the textfield is empty
    public String generateCampaignName() {
        String name = campaignNameField.getText();
        return name.isBlank() ? "Campaign " + (query.selectCount().from(CAMPAIGN).fetchOne(DSL.count()) + 1) : name;
    }

    // inserts a new entry to the Campaign table and sets the uniquely generated id as a member variable
    private void insertCampaign() {
        String campaignName = generateCampaignName();
        campaignID = query
                .insertInto(CAMPAIGN, CAMPAIGN.NAME)
                .values(campaignName)
                .returningResult(CAMPAIGN.CID)
                .fetchOne().value1();
        System.out.println("Added new campaign \"" + campaignName + "\" with id: " + campaignID);
    }

    public void importFolder() {
        if (isValidFolder(folder)) {

            insertCampaign();

            Arrays.stream(Objects.requireNonNull(folder.listFiles())).forEach(file -> {
                try {
                    String name = file.getName();
                    if (name.equals("impression_log.csv"))
                        executor.execute(() -> parse(file.getAbsolutePath(), toImpression(query, campaignID)));
                    if (name.equals("click_log.csv"))
                        executor.execute(() -> parse(file.getAbsolutePath(), toClick(query, campaignID)));
                    if (name.equals("server_log.csv"))
                        executor.execute(() -> parse(file.getAbsolutePath(), toInteraction(query, campaignID)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            //update progress here
        } else return;
        exit();
    }

    public void importFiles() {
        String impressions = impressionLogPath.getText();
        String clicks = clickLogPath.getText();
        String interactions = interactionLogPath.getText();
        if (!impressions.isBlank() && !clicks.isBlank() && !interactions.isBlank()) {
            if (Files.isReadable(Paths.get(impressions))
                    && Files.isReadable(Paths.get(clicks))
                    && Files.isReadable(Paths.get(interactions))) {

                insertCampaign();

                executor.execute(() -> parse(impressions, toImpression(query, campaignID)));
                executor.execute(() -> parse(clicks, toClick(query, campaignID)));
                executor.execute(() -> parse(interactions, toInteraction(query, campaignID)));
                //update progress here
                exit();
            }
        }
    }

    private void exit() {
        executor.shutdown();
        Stage stage = (Stage) importForm.getScene().getWindow();
        stage.close();
    }

    public void selectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
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
        fileChooser.setTitle("Select " + fileType);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(fileType, "*.csv"));
        File log = fileChooser.showOpenDialog(importForm.getScene().getWindow());
        if (log == null) return;
        pathField.setText(log.getAbsolutePath());
    }

    @FXML
    public void initialize() {
        query = DSL.using(DB.connection(), SQLDialect.H2);
        executor = Executors.newWorkStealingPool();

        //changes view depending on combobox selection
        source.getSelectionModel().selectedIndexProperty().addListener((p, o, newval) -> {
            panes.getChildren().forEach(node -> node.setVisible(false));
            panes.getChildren().get(newval.intValue()).setVisible(true);
        });
        //initial selection
        source.getSelectionModel().selectFirst();

    }

    public void parse(String path, Consumer<String> consumer) {
        System.out.println("Debug: Attempted parsing of " + path);
    }

    public void parse2(String path, Consumer<String> consumer) throws Exception {
        File file = new File(path);

        System.out.println("Parsing " + file.getAbsolutePath());
        long startTime = System.currentTimeMillis();

        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        input.lines().parallel().skip(1).forEach(consumer);
        input.close();

        long endTime = System.currentTimeMillis();
        System.out.println("Done parsing " + file.getAbsolutePath() + " in " + (endTime - startTime) + "ms");
    }

    public void selectZip() {
        //TODO
    }

    public void importZip() {
        //TODO
    }
}
