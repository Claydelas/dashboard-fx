package group18.dashboard.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.validation.RequiredFieldValidator;
import group18.dashboard.App;
import group18.dashboard.TimeGranularity;
import group18.dashboard.ViewDataParser;
import group18.dashboard.database.enums.ImpressionAge;
import group18.dashboard.database.enums.ImpressionContext;
import group18.dashboard.database.enums.ImpressionGender;
import group18.dashboard.database.enums.ImpressionIncome;
import group18.dashboard.database.tables.records.ClickRecord;
import group18.dashboard.database.tables.records.ImpressionRecord;
import group18.dashboard.database.tables.records.InteractionRecord;
import group18.dashboard.util.PngEncoderFX;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static group18.dashboard.App.query;
import static group18.dashboard.database.tables.Campaign.CAMPAIGN;
import static group18.dashboard.database.tables.Click.CLICK;
import static group18.dashboard.database.tables.Impression.IMPRESSION;
import static group18.dashboard.database.tables.Interaction.INTERACTION;
import static org.jooq.impl.DSL.select;

public class ChartFactory {

    public static final StringConverter<LocalDate> dateConverter = new StringConverter<>() {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        @Override
        public String toString(LocalDate date) {
            if (date != null) {
                return dateFormatter.format(date);
            } else {
                return "";
            }
        }

        @Override
        public LocalDate fromString(String string) {
            if (string != null && !string.isEmpty()) {
                return LocalDate.parse(string, dateFormatter);
            } else {
                return null;
            }
        }
    };
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
    public DatePicker fromDate;
    public DatePicker toDate;
    public JFXComboBox<String> granularity;
    ExecutorService executor;
    private FlowPane dashboardArea;
    private ObservableList<String> metrics;
    private ObservableList<String> granularities;

    @FXML
    public void initialize() {
        metrics = FXCollections.observableArrayList("Impressions",
                "Clicks", "Uniques", "Bounces", "Conversions", "Total Cost", "Click-through-rate",
                "Cost-per-acquisition", "Cost-per-click", "Cost-per-mille", "Bounce Rate");
        granularities = FXCollections.observableArrayList("Hourly", "Daily", "Weekly");

        chartTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection.equals("Histogram")) {
                metricComboBox.setItems(FXCollections.observableArrayList("Total click costs", "Cost per click"));
                granularity.setItems(FXCollections.observableArrayList("Hourly", "Daily"));
            } else {
                metricComboBox.setItems(metrics);
                granularity.setItems(granularities);
            }
        });

        fromDate.setConverter(dateConverter);
        toDate.setConverter(dateConverter);

        chartTypeComboBox.getSelectionModel().select(0);
        granularity.getSelectionModel().select(1);

        executor = Executors.newWorkStealingPool();

        metricComboBox.setValidators(new RequiredFieldValidator());
        campaignComboBox.setValidators(new RequiredFieldValidator());
        chartTypeComboBox.setValidators(new RequiredFieldValidator());
    }

    public void setChartPane(FlowPane p) {
        this.dashboardArea = p;
    }

    public void addChartAction() {
        // validates all required fields on entry
        if (!chartTypeComboBox.validate() || !campaignComboBox.validate() || !metricComboBox.validate()) return;

        final String campaignName = campaignComboBox.getSelectionModel().getSelectedItem();
        final int campaignID = query
                .select(CAMPAIGN.CID)
                .from(CAMPAIGN)
                .where(CAMPAIGN.NAME.eq(campaignName))
                .fetchOne()
                .value1();

        executor.execute(() -> {

            final XYChart<String, Number> chart;

            String chartType = chartTypeComboBox.getSelectionModel().getSelectedItem();
            // CHART TYPE SELECTION LOGIC
            switch (chartType) {
                case "Line Chart":
                    chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
                    //((LineChart<String, Number>) chart).setCreateSymbols(false);
                    System.out.println("Debug : LINE CHART");
                    break;
                case "Histogram":
                    chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
                    ((BarChart<String, Number>) chart).setBarGap(0);
                    System.out.println("Debug : HISTOGRAM");
                    break;
                default:
                    chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
            }

            ProgressIndicator progress = new ProgressIndicator();
            progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            final StackPane pane = new StackPane(progress);
            StackPane.setAlignment(progress, Pos.CENTER);

            Platform.runLater(() -> dashboardArea.getChildren().add(pane));

            TimeGranularity timeGranularity = Enum.valueOf(TimeGranularity.class, granularity.getSelectionModel().getSelectedItem().toUpperCase());
            if (timeGranularity.equals(TimeGranularity.HOURLY) && chart instanceof LineChart) {
                ((LineChart<String, Number>) chart).setCreateSymbols(false);
                chart.setVerticalGridLinesVisible(false);
            }
            // METRIC SELECTION LOGIC
            XYChart.Series<String, Number> series = null;
            final Condition filter = getFilter();
            final String filterString = getFilterString(filter);
            switch (metricComboBox.getSelectionModel().getSelectedItem()) {
                case "Impressions":
                    series = ViewDataParser.getSeriesOf(
                            "Impressions\n" + filterString,
                            timeGranularity,
                            query
                                    .select(IMPRESSION.DATE)
                                    .from(IMPRESSION)
                                    .where(IMPRESSION.CID.eq(campaignID)
                                            .and(filter)
                                            .and(getDateRange(IMPRESSION.DATE)))
                                    .fetch(IMPRESSION.DATE)
                    );
                    break;
                case "Total click costs":
                    chart.getYAxis().setLabel("Total click costs (£)");
                    if (timeGranularity.equals(TimeGranularity.DAILY)) {
                        series = ViewDataParser.getDailyClickCostsHistogram(filterString,
                                fetchClicks(campaignID), false
                        );
                    }
                    if (timeGranularity.equals(TimeGranularity.HOURLY)) {
                        series = ViewDataParser.getHourlyClickCostsHistogram(filterString,
                                fetchClicks(campaignID), false
                        );
                    }
                    break;
                case "Cost per click":
                    chart.getYAxis().setLabel("Cost per click (£)");
                    if (timeGranularity.equals(TimeGranularity.DAILY)) {
                        series = ViewDataParser.getDailyClickCostsHistogram(filterString,
                                fetchClicks(campaignID), true
                        );
                    }
                    if (timeGranularity.equals(TimeGranularity.HOURLY)) {
                        series = ViewDataParser.getHourlyClickCostsHistogram(filterString,
                                fetchClicks(campaignID), true
                        );
                    }
                    break;
                case "Clicks":
                    series = ViewDataParser.getSeriesOf(
                            "Clicks\n" + filterString,
                            timeGranularity,
                            query
                                    .select(CLICK.DATE)
                                    .from(CLICK)
                                    .where(CLICK.CID.eq(campaignID)
                                            .and(CLICK.USER.in(
                                                    select(IMPRESSION.USER).from(IMPRESSION).where(IMPRESSION.CID.eq(campaignID).and(filter))))
                                            .and(getDateRange(CLICK.DATE)))
                                    .fetch(CLICK.DATE)
                    );
                    break;
                case "Uniques":
                    series = ViewDataParser.getSeriesOf("Uniques\n" + filterString,
                            timeGranularity,
                            query
                                    .select(CLICK.DATE)
                                    .distinctOn(CLICK.USER)
                                    .from(CLICK)
                                    .where(CLICK.CID.eq(campaignID)
                                            .and(CLICK.USER.in(
                                                    select(IMPRESSION.USER)
                                                            .from(IMPRESSION)
                                                            .where(IMPRESSION.CID.eq(campaignID).and(filter))))
                                            .and(getDateRange(CLICK.DATE)))
                                    .fetch(CLICK.DATE)
                    );
                    break;
                case "Bounces":
                    series = ViewDataParser.getSeriesOf("Bounces\n" + filterString,
                            timeGranularity,
                            query.select(INTERACTION.ENTRY_DATE)
                                    .from(INTERACTION)
                                    .where(INTERACTION.CID.eq(campaignID)
                                            .and(INTERACTION.CONVERSION.isFalse())
                                            .and(INTERACTION.USER.in(
                                                    select(IMPRESSION.USER).from(IMPRESSION).where(IMPRESSION.CID.eq(campaignID).and(filter))))
                                            .and(getDateRange(INTERACTION.ENTRY_DATE)))
                                    .fetch(INTERACTION.ENTRY_DATE)
                    );
                    break;
                case "Conversions":
                    series = ViewDataParser.getSeriesOf("Conversions\n" + filterString,
                            timeGranularity,
                            query.select(INTERACTION.ENTRY_DATE)
                                    .from(INTERACTION)
                                    .where(INTERACTION.CID.eq(campaignID)
                                            .and(INTERACTION.CONVERSION)
                                            .and(INTERACTION.USER.in(
                                                    select(IMPRESSION.USER).from(IMPRESSION).where(IMPRESSION.CID.eq(campaignID).and(filter))))
                                            .and(getDateRange(INTERACTION.ENTRY_DATE)))
                                    .fetch(INTERACTION.ENTRY_DATE)
                    );
                    break;
                case "Total Cost":
                    series = ViewDataParser.getTotalCostSeries(filterString, timeGranularity
                            , fetchImpressions(campaignID)
                            , fetchClicks(campaignID)
                    );
                    break;
                case "Click-through-rate":
                    series = ViewDataParser.getCTRTimeSeries(filterString, timeGranularity,
                            fetchImpressions(campaignID)
                            , fetchClicks(campaignID)
                    );
                    break;
                case "Cost-per-acquisition":
                    series = ViewDataParser.getCPATimeSeries(filterString, timeGranularity
                            , fetchImpressions(campaignID)
                            , fetchClicks(campaignID)
                            , fetchInteractions(campaignID)
                    );
                    break;
                case "Cost-per-click":
                    series = ViewDataParser.getCPCTimeSeries(filterString, timeGranularity
                            , fetchImpressions(campaignID)
                            , fetchClicks(campaignID)
                    );
                    break;
                case "Cost-per-mille":
                    series = ViewDataParser.getCPMTimeSeries(filterString, timeGranularity
                            , fetchImpressions(campaignID)
                            , fetchClicks(campaignID)
                    );
                    break;
                case "Bounce Rate":
                    series = ViewDataParser.getBounceRateTimeSeries(filterString, timeGranularity
                            , fetchClicks(campaignID)
                            , fetchInteractions(campaignID)
                    );
                    break;
            }
            chart.getData().add(series);

            final JFXButton png = new JFXButton("PNG");
            final JFXButton close = new JFXButton();
            ImageView image = new ImageView(App.class.getResource("icons/baseline_cancel_black_18dp.png").toString());
            image.setFitWidth(20);
            image.setFitHeight(20);
            image.setPreserveRatio(true);
            close.setGraphic(image);

            final VBox buttons = new VBox(close, png);
            buttons.setAlignment(Pos.TOP_RIGHT);
            StackPane.setAlignment(buttons, Pos.TOP_RIGHT);

            Platform.runLater(() -> {
                pane.getChildren().clear();
                pane.getChildren().addAll(chart, buttons);
            });

            png.setOnMouseClicked(e -> {
                saveAsPng(chart);
                dashboardArea.requestFocus();
            });
            close.setOnMouseClicked(e -> dashboardArea.getChildren().remove(pane));

            makeDraggable(pane);
        });
        executor.shutdown();
        //implicit closing of stage after a chart is added
        cancelAction();
    }

    private Result<ImpressionRecord> fetchImpressions(int campaignID) {
        return query
                .selectFrom(IMPRESSION)
                .where(IMPRESSION.CID.eq(campaignID)
                        .and(getFilter())
                        .and(getDateRange(IMPRESSION.DATE)))
                .fetch();
    }

    private Result<InteractionRecord> fetchInteractions(int campaignID) {
        return query
                .selectFrom(INTERACTION)
                .where(INTERACTION.CID.eq(campaignID)
                        .and(INTERACTION.USER.in(
                                select(IMPRESSION.USER)
                                        .from(IMPRESSION)
                                        .where(IMPRESSION.CID.eq(campaignID)
                                                .and(getFilter()))))
                        .and(getDateRange(INTERACTION.ENTRY_DATE)))
                .fetch();
    }

    private Result<ClickRecord> fetchClicks(int campaignID) {
        return query
                .selectFrom(CLICK)
                .where(CLICK.CID.eq(campaignID)
                        .and(CLICK.USER.in(
                                select(IMPRESSION.USER)
                                        .from(IMPRESSION)
                                        .where(IMPRESSION.CID.eq(campaignID)
                                                .and(getFilter()))))
                        .and(getDateRange(CLICK.DATE)))
                .fetch();
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

    public Condition getFilter() {
        Condition filter = DSL.noCondition();

        Condition subcondition = DSL.noCondition();
        if (filterBelow25.isSelected()) subcondition = subcondition.or(IMPRESSION.AGE.eq(ImpressionAge._3c25));
        if (filter25to34.isSelected()) subcondition = subcondition.or(IMPRESSION.AGE.eq(ImpressionAge._25_34));
        if (filter35to44.isSelected()) subcondition = subcondition.or(IMPRESSION.AGE.eq(ImpressionAge._35_44));
        if (filter45to54.isSelected()) subcondition = subcondition.or(IMPRESSION.AGE.eq(ImpressionAge._45_54));
        if (filterAbove54.isSelected()) subcondition = subcondition.or(IMPRESSION.AGE.eq(ImpressionAge._3e54));
        filter = filter.and(subcondition);

        subcondition = DSL.noCondition();
        if (filterBlog.isSelected()) subcondition = subcondition.or(IMPRESSION.CONTEXT.eq(ImpressionContext.Blog));
        if (filterHobbies.isSelected())
            subcondition = subcondition.or(IMPRESSION.CONTEXT.eq(ImpressionContext.Hobbies));
        if (filterNews.isSelected()) subcondition = subcondition.or(IMPRESSION.CONTEXT.eq(ImpressionContext.News));
        if (filterShopping.isSelected())
            subcondition = subcondition.or(IMPRESSION.CONTEXT.eq(ImpressionContext.Shopping));
        if (filterSocialMedia.isSelected())
            subcondition = subcondition.or(IMPRESSION.CONTEXT.eq(ImpressionContext.Social_Media));
        if (filterTravel.isSelected()) subcondition = subcondition.or(IMPRESSION.CONTEXT.eq(ImpressionContext.Travel));
        filter = filter.and(subcondition);

        subcondition = DSL.noCondition();
        if (filterMale.isSelected()) subcondition = subcondition.or(IMPRESSION.GENDER.eq(ImpressionGender.Male));
        if (filterFemale.isSelected()) subcondition = subcondition.or(IMPRESSION.GENDER.eq(ImpressionGender.Female));
        filter = filter.and(subcondition);

        subcondition = DSL.noCondition();
        if (filterLow.isSelected()) subcondition = subcondition.or(IMPRESSION.INCOME.eq(ImpressionIncome.Low));
        if (filterMedium.isSelected()) subcondition = subcondition.or(IMPRESSION.INCOME.eq(ImpressionIncome.Medium));
        if (filterHigh.isSelected()) subcondition = subcondition.or(IMPRESSION.INCOME.eq(ImpressionIncome.High));
        filter = filter.and(subcondition);

        System.out.println("Applied filter: " + filter);
        return filter;
    }

    private String getFilterString(Condition filterCondition) {
        final String filter = filterCondition.toString();
        final StringBuilder filterString = new StringBuilder("\n");
        filterString.append("Filters: \n");

        final Matcher m = Pattern.compile("(?:\"IMPRESSION\"\\.\")([A-Z]+)(?:\" = ')([^']+)").matcher(filter);

        while (m.find()) {
            filterString
                    .append(m.group(1).substring(0, 1).toUpperCase())
                    .append(m.group(1).substring(1).toLowerCase())
                    .append(": ");
            filterString.append(m.group(2)).append("\n");
        }

        return filterString.toString();
    }

    public <R extends Record> Condition getDateRange(TableField<R, LocalDateTime> field) {
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        if (from != null && to != null) {
            return field.between(from.atStartOfDay(), to.atStartOfDay());
        }
        if (from != null) {
            return field.ge(from.atStartOfDay());
        }
        if (to != null) {
            return field.lt(to.atStartOfDay());
        }
        return DSL.noCondition();
    }

    public void setCampaigns(ObservableList<String> campaigns) {
        campaignComboBox.setItems(campaigns);
    }

    private void saveAsPng(Chart chart) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString()));
        fileChooser.setInitialFileName("Untitled");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = fileChooser.showSaveDialog(dashboardArea.getScene().getWindow());
        if (file != null) {
            try {
                exportPngSnapshot(chart, file.toPath(), Color.WHITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportPngSnapshot(Node node, Path path, Paint backgroundFill) throws IOException {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(backgroundFill);
        Image chartSnapshot = node.snapshot(params, null);
        PngEncoderFX encoder = new PngEncoderFX(chartSnapshot, true);
        byte[] bytes = encoder.pngEncode();
        Files.write(path, bytes);
    }
}
