package group18.dashboard.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class ChartFactory {

    private FlowPane dashboardArea;

    @FXML
    public void initialize() {

    }
    public void setChartPane(FlowPane p){
        this.dashboardArea = p;
    }
    public void okButton(){
        final XYChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        makeDraggable(chart);
        dashboardArea.getChildren().add(chart);
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
}
