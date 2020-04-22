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
import group18.dashboard.util.DB;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        DSLContext query = DSL.using(DB.connection(), SQLDialect.H2);

        executor.execute(() -> {
            /*Campaign temp = new Campaign();
            try {
                temp.updateImpressions("D:\\Projects\\Java\\2_week_campaign");
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            final XYChart<String, Number> chart;

            switch (chartTypeComboBox.getSelectionModel().getSelectedItem()) {
                case "Line Chart":
                    chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
                    System.out.println("Debug : LINE CHART");
                    break;
                default:
                    chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
            }

            //ObservableList<XYChart.Data<String, Number>> data = FXCollections.observableArrayList();

            switch (metricComboBox.getSelectionModel().getSelectedItem()) {
                case "Impressions":
                    chart.getData().add(ViewDataParser.getSeriesOf(
                            "Impressions",
                            query
                                    .select()
                                    .from(IMPRESSION)
                                    .where(getFilter())
                                    .fetch(IMPRESSION.DATE)));
                    break;
                case "Clicks":
                    chart.getData().add(ViewDataParser.getSeriesOf(
                            "Clicks",
                            query
                                    .select()
                                    .from(CLICK)
                                    .where(CLICK.USER.in(
                                            select(IMPRESSION.USER)
                                                    .from(IMPRESSION)
                                                    .where(getFilter())))
                                    .fetch(CLICK.DATE)));
                    break;
                case "Uniques":
                    chart.getData().add(ViewDataParser.getSeriesOf("Uniques",
                            query
                                    .selectDistinct(CLICK.DATE)
                                    .from(CLICK)
                                    .where(CLICK.USER.in(
                                            select(IMPRESSION.USER)
                                                    .from(IMPRESSION)
                                                    .where(getFilter())))
                                    .fetch(CLICK.DATE)));
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

        System.out.println(filter);
        return filter;
    }
}
