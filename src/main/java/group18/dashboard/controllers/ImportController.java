package group18.dashboard.controllers;

import com.jfoenix.controls.JFXComboBox;
import group18.dashboard.database.enums.ImpressionAge;
import group18.dashboard.database.enums.ImpressionContext;
import group18.dashboard.database.enums.ImpressionGender;
import group18.dashboard.database.enums.ImpressionIncome;
import group18.dashboard.model.Campaign;
import group18.dashboard.util.DB;
import group18.dashboard.util.Parsing;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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
    public GridPane importForm;
    public JFXComboBox<String> source;
    public StackPane panes;
    public TextField zipPath;
    public Button browseZip;
    public TextField campaignNameField;

    DSLContext query;
    ExecutorService executor;
    DashboardController parentController;
    File folder;


    private Consumer<String> toClick = (line) -> {
        String[] columns = line.split(",");

        query.insertInto(CLICK,
                CLICK.DATE, CLICK.USER, CLICK.COST, CLICK.CID)
                .values(
                        LocalDateTime.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        Long.valueOf(columns[1]),
                        Double.valueOf(columns[2]),
                        1)
                .execute();
    };
    private Consumer<String> toInteraction = (line) -> {
        String[] columns = line.split(",");

        query.insertInto(INTERACTION,
                INTERACTION.ENTRY_DATE, INTERACTION.USER, INTERACTION.EXIT_DATE,
                INTERACTION.VIEWS, INTERACTION.CONVERSION, INTERACTION.CID)
                .values(
                        LocalDateTime.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        Long.valueOf(columns[1]),
                        !columns[2].equals("n/a") ? LocalDateTime.parse(columns[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null,
                        Integer.parseInt(columns[3]),
                        columns[4].equalsIgnoreCase("Yes"),
                        1)
                .execute();
    };
    private Consumer<String> toImpression = (line) -> {
        String[] columns = line.split(",");

        query.insertInto(IMPRESSION,
                IMPRESSION.DATE, IMPRESSION.USER, IMPRESSION.GENDER, IMPRESSION.AGE,
                IMPRESSION.INCOME, IMPRESSION.CONTEXT, IMPRESSION.COST, IMPRESSION.CID)
                .values(
                        LocalDateTime.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        Long.valueOf(columns[1]),
                        ImpressionGender.valueOf(columns[2]),
                        (ImpressionAge) Parsing.toEnum(columns[3]),
                        ImpressionIncome.valueOf(columns[4]),
                        (ImpressionContext) Parsing.toEnum(columns[5]),
                        Double.valueOf(columns[6]),
                        1)
                .execute();
    };

    boolean isValidFolder(File dir) {
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && !(Arrays.stream(files).allMatch(file -> file.getName().equals("click_log.csv")
                    || file.getName().equals("impression_log.csv") || file.getName().equals("server_log.csv")))) {
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

    public String generateCampaignName(){
        String name = campaignNameField.getText();
        return name.isBlank() ? "Campaign " + (query.selectCount().from(CAMPAIGN).fetchOne(DSL.count()) + 1) : name;
    }

    public void importFolder() {
        if (isValidFolder(folder)) {
            Arrays.stream(folder.listFiles()).forEach(file -> {
                try {
                    String name = file.getName();
                    if (name.equals("impression_log.csv"))
                        executor.execute(() -> parse(file.getAbsolutePath(), toImpression));
                    if (name.equals("click_log.csv"))
                        executor.execute(() -> parse(file.getAbsolutePath(), toClick));
                    if (name.equals("server_log.csv"))
                        executor.execute(() -> parse(file.getAbsolutePath(), toInteraction));
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
                executor.execute(() -> parse(impressions, toImpression));
                executor.execute(() -> parse(clicks, toClick));
                executor.execute(() -> parse(interactions, toInteraction));
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
        System.out.println("tried parsing " + path);
    }

    public void parse2(String path, Consumer<String> consumer) throws Exception {
        File file = new File(path);
        System.out.println("Parsing " + file.getAbsolutePath());
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        input.lines().parallel().skip(1).forEach(consumer);
        input.close();
        System.out.println("Done parsing " + file.getAbsolutePath());
    }

    public void importCampaign() {

        System.out.println("--Parsing Campaign--");

        CountDownLatch latch = new CountDownLatch(3);
        Campaign in = new Campaign();

        executor.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();
                in.updateClicks(folder.getAbsolutePath());
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
                in.updateImpressions(folder.getAbsolutePath());
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
                in.updateInteractions(folder.getAbsolutePath());
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
            parentController.updateMetrics(executor);
        });
        executor.shutdown();
    }

    public void selectZip() {

    }

    public void importZip() {

    }
}
