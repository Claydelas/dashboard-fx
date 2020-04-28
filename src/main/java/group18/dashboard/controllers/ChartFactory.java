package group18.dashboard.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.validation.RequiredFieldValidator;
import group18.dashboard.App;
import group18.dashboard.ViewDataParser;
import group18.dashboard.database.enums.ImpressionAge;
import group18.dashboard.database.enums.ImpressionContext;
import group18.dashboard.database.enums.ImpressionGender;
import group18.dashboard.database.enums.ImpressionIncome;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static group18.dashboard.App.query;
import static group18.dashboard.database.tables.Campaign.CAMPAIGN;
import static group18.dashboard.database.tables.Click.CLICK;
import static group18.dashboard.database.tables.Impression.IMPRESSION;
import static org.jooq.impl.DSL.select;

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
    public DatePicker fromDate;
    public DatePicker toDate;
    public JFXComboBox<String> granularity;
    ExecutorService executor;
    private FlowPane dashboardArea;

    @FXML
    public void initialize() {
        executor = Executors.newWorkStealingPool();

        granularity.getSelectionModel().select(1);

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

            // CHART TYPE SELECTION LOGIC
            switch (chartTypeComboBox.getSelectionModel().getSelectedItem()) {
                case "Line Chart":
                    chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
                    System.out.println("Debug : LINE CHART");
                    break;
                default:
                    chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
            }

            // METRIC SELECTION LOGIC
            switch (metricComboBox.getSelectionModel().getSelectedItem()) {
                case "Impressions":
                    chart.getData().add(ViewDataParser.getSeriesOf(
                            "Impressions",
                            query
                                    .select(IMPRESSION.DATE)
                                    .from(IMPRESSION)
                                    .where(IMPRESSION.CID.eq(campaignID)
                                            .and(getFilter())
                                            .and(getDateRange(IMPRESSION.DATE)))
                                    .fetch(IMPRESSION.DATE)));
                    break;
                case "Clicks":
                    chart.getData().add(ViewDataParser.getSeriesOf(
                            "Clicks",
                            query
                                    .select(CLICK.DATE)
                                    .from(CLICK)
                                    .where(CLICK.CID.eq(campaignID)
                                            .and(CLICK.USER.in(
                                                    select(IMPRESSION.USER)
                                                            .from(IMPRESSION)
                                                            .where(IMPRESSION.CID.eq(campaignID)
                                                                    .and(getFilter()))))
                                            .and(getDateRange(CLICK.DATE)))
                                    .fetch(CLICK.DATE)));
                    break;
                case "Uniques":
                    chart.getData().add(ViewDataParser.getSeriesOf("Uniques",
                            query
                                    .select(CLICK.DATE)
                                    .distinctOn(CLICK.USER)
                                    .from(CLICK)
                                    .where(CLICK.CID.eq(campaignID)
                                            .and(CLICK.USER.in(
                                                    select(IMPRESSION.USER)
                                                            .from(IMPRESSION)
                                                            .where(IMPRESSION.CID.eq(campaignID)
                                                                    .and(getFilter()))))
                                            .and(getDateRange(CLICK.DATE)))
                                    .fetch(CLICK.DATE)));
                    break;
                case "Bounces":
                    //TODO Bounces Series
                    break;
                case "Conversions":
                    //TODO Conversions Series
                    break;
                case "Total Cost":
                    //TODO Total Cost Series
                    break;
                case "Click-through-rate":
                    //TODO Click-through-rate Series
                    break;
                case "Cost-per-acquisition":
                    //TODO Cost-per-acquisition Series
                    break;
                case "Cost-per-click":
                    //TODO Cost-per-click Series
                    break;
                case "Cost-per-mille":
                    //TODO Cost-per-mille Series
                    break;
                case "Bounce Rate":
                    //TODO Bounce Rate Series
                    break;
            }
            final JFXButton close = new JFXButton();
            ImageView image = new ImageView(App.class.getResource("icons/baseline_cancel_black_18dp.png").toString());
            image.setFitWidth(20);
            image.setFitHeight(20);
            image.setPreserveRatio(true);
            close.setGraphic(image);

            final StackPane pane = new StackPane(chart, close);

            StackPane.setAlignment(close, Pos.TOP_RIGHT);

            close.setOnMouseClicked(e -> dashboardArea.getChildren().remove(pane));
            makeDraggable(pane);
            Platform.runLater(() -> dashboardArea.getChildren().add(pane));
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
}
