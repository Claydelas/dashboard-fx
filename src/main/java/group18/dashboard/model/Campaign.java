package group18.dashboard.model;

import group18.dashboard.ViewDataParser;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    private XYChart.Series<String, Number> ctrSeries;
    private XYChart.Series<String, Number> cpmSeries;
    private XYChart.Series<String, Number> cpaSeries;
    private XYChart.Series<String, Number> cpcSeries;
    private XYChart.Series<String, Number> bounceRateSeries;
    private XYChart.Series<String, Number> uniquesSeries;
    private XYChart.Series<String, Number> impressionSeries;
    private XYChart.Series<String, Number> clickCountSeries;
    private XYChart.Series<String, Number> bouncesSeries;
    private XYChart.Series<String, Number> conversionSeries;
    private XYChart.Series<String, Number> totalCostSeries;

    private static void benchmarks(String path) throws Exception {
        System.out.println("----- TIMINGS ------\n");

        long t = System.currentTimeMillis();
        Campaign c = new Campaign();
        c.readClicks(path);
        c.readImpressions(path);
        c.readInteractions(path);
        System.out.printf("Initial CSV load: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        System.out.println("\n----- ONLY DATA ------\n"); // All negligible using 2-week sample data

        t = System.currentTimeMillis();
        ViewDataParser.getUniques(c.getClicks());
        System.out.printf("getUniques: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getBounces(c.getInteractions());
        System.out.printf("getBounces: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getConversions(c.getInteractions());
        System.out.printf("getConversions: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getTotalCost(c.getImpressions(), c.getClicks());
        System.out.printf("getTotalCost: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCTR(c.getImpressions(), c.getClicks());
        System.out.printf("getCTR: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCPA(c.getImpressions(), c.getClicks(), c.getInteractions());
        System.out.printf("getCPA: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCPC(c.getImpressions(), c.getClicks());
        System.out.printf("getCPC: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCPM(c.getImpressions(), c.getClicks());
        System.out.printf("getCPM: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getBounceRate(c.getClicks(), c.getInteractions());
        System.out.printf("getBounceRate: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);


        System.out.println("\n----- TIME SERIES ------\n");

        t = System.currentTimeMillis();
        ViewDataParser.getCumulativeTimeSeries("Impressions", Calendar.DAY_OF_MONTH,
                c.getImpressions().parallelStream().map(Impression::getDate).collect(Collectors.toList()));
        System.out.printf("getCumulativeTimeSeries (Impressions): %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCumulativeTimeSeries("Clicks", Calendar.DAY_OF_MONTH,
                c.getClicks().parallelStream().map(Click::getDate).collect(Collectors.toList()));
        System.out.printf("getCumulativeTimeSeries (Clicks): %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCumulativeTimeSeries("Uniques", Calendar.DAY_OF_MONTH,
                c.getClicks().parallelStream().filter(distinctByKey(Click::getDate)).map(Click::getDate).collect(Collectors.toList()));
        System.out.printf("getCumulativeTimeSeries (Uniques): %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        // TODO the rest

        t = System.currentTimeMillis();
        ViewDataParser.getCTRTimeSeries(Calendar.DAY_OF_MONTH, c.getImpressions(), c.getClicks());
        System.out.printf("getCTRTimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getTotalCostSeries(Calendar.DAY_OF_MONTH, c.getImpressions(), c.getClicks());
        System.out.printf("getTotalCostSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCPMTimeSeries(c.getImpressions(), c.getClicks());
        System.out.printf("getCPMTimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getBounceRateTimeSeries(Calendar.DAY_OF_MONTH, c.getClicks(), c.getInteractions());
        System.out.printf("getBounceRateTimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis(); // TODO take less than 15s (not sure how)
        ViewDataParser.getCPCTimeSeries(Calendar.DAY_OF_MONTH, c.getImpressions(), c.getClicks());
        System.out.printf("getCPCTimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis(); // TODO take less than 10s (easy)
        ViewDataParser.getCPATimeSeries(c.getImpressions(), c.getClicks(), c.getInteractions());
        System.out.printf("getCPATimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public XYChart.Series<String, Number> getCtrSeries() {
        return ctrSeries;
    }

    public void setCtrSeries(XYChart.Series<String, Number> ctrSeries) {
        this.ctrSeries = ctrSeries;
    }

    public XYChart.Series<String, Number> getTotalCostSeries() {
        return totalCostSeries;
    }

    public void setTotalCostSeries(XYChart.Series<String, Number> totalCostSeries) {
        this.totalCostSeries = totalCostSeries;
    }

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

    public XYChart.Series<String, Number> getCPMSeries() {
        return cpmSeries;
    }

    public void setCPMSeries(XYChart.Series<String, Number> cpmSeries) {
        this.cpmSeries = cpmSeries;
    }

    public XYChart.Series<String, Number> getCPASeries() {
        return cpaSeries;
    }

    public void setCPASeries(XYChart.Series<String, Number> cpaSeries) {
        this.cpaSeries = cpaSeries;
    }

    public XYChart.Series<String, Number> getCPCSeries() {
        return cpcSeries;
    }

    public void setCPCSeries(XYChart.Series<String, Number> cpcSeries) {
        this.cpcSeries = cpcSeries;
    }

    public XYChart.Series<String, Number> getBounceRateSeries() {
        return bounceRateSeries;
    }

    public void setBounceRateSeries(XYChart.Series<String, Number> bounceRateSeries) {
        this.bounceRateSeries = bounceRateSeries;
    }

    public XYChart.Series<String, Number> getUniquesSeries() {
        return uniquesSeries;
    }

    public void setUniquesSeries(XYChart.Series<String, Number> uniquesSeries) {
        this.uniquesSeries = uniquesSeries;
    }

    public XYChart.Series<String, Number> getImpressionSeries() {
        return impressionSeries;
    }

    public void setImpressionSeries(XYChart.Series<String, Number> impressionSeries) {
        this.impressionSeries = impressionSeries;
    }

    public XYChart.Series<String, Number> getClickCountSeries() {
        return clickCountSeries;
    }

    public void setClickCountSeries(XYChart.Series<String, Number> clickCountSeries) {
        this.clickCountSeries = clickCountSeries;
    }

    public XYChart.Series<String, Number> getBouncesSeries() {
        return bouncesSeries;
    }

    public void setBouncesSeries(XYChart.Series<String, Number> bouncesSeries) {
        this.bouncesSeries = bouncesSeries;
    }

    public XYChart.Series<String, Number> getConversionSeries() {
        return conversionSeries;
    }

    public void setConversionSeries(XYChart.Series<String, Number> conversionSeries) {
        this.conversionSeries = conversionSeries;
    }
}
