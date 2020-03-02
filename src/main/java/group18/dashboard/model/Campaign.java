package group18.dashboard.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Campaign {

    private ObservableList<Impression> impressions = FXCollections.observableArrayList();
    private ObservableList<Click> clicks = FXCollections.observableArrayList();
    private ObservableList<Interaction> interactions = FXCollections.observableArrayList();
    private SimpleLongProperty impressionCount = new SimpleLongProperty();
    private SimpleLongProperty clickCount = new SimpleLongProperty();
    private SimpleLongProperty uniques = new SimpleLongProperty();
    private SimpleLongProperty bounces = new SimpleLongProperty();
    private SimpleLongProperty conversions = new SimpleLongProperty();
    private SimpleDoubleProperty totalCost = new SimpleDoubleProperty();
    private SimpleDoubleProperty ctr = new SimpleDoubleProperty();
    private SimpleDoubleProperty cpa = new SimpleDoubleProperty();
    private SimpleDoubleProperty cpc = new SimpleDoubleProperty();
    private SimpleDoubleProperty cpm = new SimpleDoubleProperty();
    private SimpleDoubleProperty bounceRate = new SimpleDoubleProperty();

    public SimpleLongProperty impressionCountProperty() {
        return impressionCount;
    }

    public SimpleLongProperty clickCountProperty() {
        return clickCount;
    }

    public SimpleLongProperty uniquesProperty() {
        return uniques;
    }

    public SimpleLongProperty bouncesProperty() {
        return bounces;
    }

    public SimpleLongProperty conversionsProperty() {
        return conversions;
    }

    public SimpleDoubleProperty totalCostProperty() {
        return totalCost;
    }

    public SimpleDoubleProperty ctrProperty() {
        return ctr;
    }

    public SimpleDoubleProperty cpaProperty() {
        return cpa;
    }

    public SimpleDoubleProperty cpcProperty() {
        return cpc;
    }

    public SimpleDoubleProperty cpmProperty() {
        return cpm;
    }

    public SimpleDoubleProperty bounceRateProperty() {
        return bounceRate;
    }

    public long getImpressionCount() {
        return impressionCount.get();
    }

    public void setImpressionCount(long impressionCount) {
        this.impressionCount.set(impressionCount);
    }

    public long getClickCount() {
        return clickCount.get();
    }

    public void setClickCount(long clickCount) {
        this.clickCount.set(clickCount);
    }

    public long getUniques() {
        return uniques.get();
    }

    public void setUniques(long uniques) {
        this.uniques.set(uniques);
    }

    public long getBounces() {
        return bounces.get();
    }

    public void setBounces(long bounces) {
        this.bounces.set(bounces);
    }

    public long getConversions() {
        return conversions.get();
    }

    public void setConversions(long conversions) {
        this.conversions.set(conversions);
    }

    public double getTotalCost() {
        return totalCost.get();
    }

    public void setTotalCost(double totalCost) {
        this.totalCost.set(totalCost);
    }

    public double getCtr() {
        return ctr.get();
    }

    public void setCtr(double ctr) {
        this.ctr.set(ctr);
    }

    public double getCpa() {
        return cpa.get();
    }

    public void setCpa(double cpa) {
        this.cpa.set(cpa);
    }

    public double getCpc() {
        return cpc.get();
    }

    public void setCpc(double cpc) {
        this.cpc.set(cpc);
    }

    public double getCpm() {
        return cpm.get();
    }

    public void setCpm(double cpm) {
        this.cpm.set(cpm);
    }

    public double getBounceRate() {
        return bounceRate.get();
    }

    public void setBounceRate(double bounceRate) {
        this.bounceRate.set(bounceRate);
    }

    public void readInteractions(String filepath) throws Exception {
        //Servers
        File serversFile = new File(filepath + File.separator + "server_log.csv");
        BufferedReader brServers = new BufferedReader(new FileReader(serversFile));
        String line = "";
        //First line is the column headings so should be ignored
        line = brServers.readLine();
        while ((line = brServers.readLine()) != null) {
            interactions.add(new Interaction((line)));
        }
    }

    public void readClicks(String filepath) throws Exception {
        //Clicks
        File clicksFile = new File(filepath + File.separator + "click_log.csv");
        BufferedReader brClicks = new BufferedReader(new FileReader(clicksFile));
        String line = "";
        //First line is the column headings so should be ignored
        line = brClicks.readLine();
        while ((line = brClicks.readLine()) != null) {
            clicks.add(new Click((line)));
        }
    }

    public void readImpressions(String filepath) throws Exception {
        //Impressions
        File impressionsFile = new File(filepath + File.separator + "impression_log.csv");
        BufferedReader brImpressions = new BufferedReader(new FileReader(impressionsFile));
        String line = "";
        //First line is the column headings so should be ignored
        line = brImpressions.readLine();
        while ((line = brImpressions.readLine()) != null) {
            impressions.add(new Impression((line)));
        }
    }

    public ObservableList<Impression> getImpressions() {
        return impressions;
    }

    public ObservableList<Click> getClicks() {
        return clicks;
    }

    public ObservableList<Interaction> getInteractions() {
        return interactions;
    }
}
