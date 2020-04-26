package group18.dashboard.model;

import group18.dashboard.ViewDataParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Campaign {

    private ObservableList<Impression> impressions = FXCollections.observableArrayList();
    private ObservableList<Click> clicks = FXCollections.observableArrayList();
    private ObservableList<Interaction> interactions = FXCollections.observableArrayList();


    public static void benchmarks(String path) throws Exception {
        System.out.println("----- TIMINGS ------\n");

        long t = System.currentTimeMillis();
        Campaign c = new Campaign();
        c.updateClicks(path);
        c.updateImpressions(path);
        c.updateInteractions(path);
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

        t = System.currentTimeMillis();
        ViewDataParser.getCumulativeTimeSeries("Bounces", Calendar.DAY_OF_MONTH,
                c.getInteractions().parallelStream().filter(i -> !i.isConversion()).map(Interaction::getEntryDate).collect(Collectors.toList()));
        System.out.printf("getCumulativeTimeSeries (Bounces): %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCumulativeTimeSeries("Conversions", Calendar.DAY_OF_MONTH,
                c.getInteractions().parallelStream().filter(Interaction::isConversion).map(Interaction::getEntryDate).collect(Collectors.toList()));
        System.out.printf("getCumulativeTimeSeries (Conversions): %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCTRTimeSeries(Calendar.DAY_OF_MONTH, c.getImpressions(), c.getClicks());
        System.out.printf("getCTRTimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getTotalCostSeries(Calendar.DAY_OF_MONTH, c.getImpressions(), c.getClicks());
        System.out.printf("getTotalCostSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCPMTimeSeries(Calendar.DAY_OF_MONTH, c.getImpressions(), c.getClicks());
        System.out.printf("getCPMTimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getBounceRateTimeSeries(Calendar.DAY_OF_MONTH, c.getClicks(), c.getInteractions());
        System.out.printf("getBounceRateTimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCPATimeSeries(Calendar.DAY_OF_MONTH, c.getImpressions(), c.getClicks(), c.getInteractions());
        System.out.printf("getCPATimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);

        t = System.currentTimeMillis();
        ViewDataParser.getCPCTimeSeries(Calendar.DAY_OF_MONTH, c.getImpressions(), c.getClicks());
        System.out.printf("getCPCTimeSeries: %.02fs%n", (System.currentTimeMillis() - t) / 1000f);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public void updateInteractions(String filepath) throws Exception {
        List<Interaction> interactions;
        File inputF = new File(filepath + File.separator + "server_log.csv");
        InputStream inputFS = new FileInputStream(inputF);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
        interactions = br.lines().parallel().skip(1).map(Interaction.mapToItem).collect(Collectors.toList());
        br.close();
        this.interactions = FXCollections.observableArrayList(interactions);
    }

    public void updateClicks(String filepath) throws Exception {
        List<Click> clicks;
        File inputF = new File(filepath + File.separator + "click_log.csv");
        InputStream inputFS = new FileInputStream(inputF);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
        clicks = br.lines().parallel().skip(1).map(Click.mapToItem).collect(Collectors.toList());
        br.close();

        this.clicks = FXCollections.observableArrayList(clicks);
    }

    public void updateImpressions(String filepath) throws Exception {
        List<Impression> impressions;
        File inputF = new File(filepath + File.separator + "impression_log.csv");
        InputStream inputFS = new FileInputStream(inputF);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
        impressions = br.lines().parallel().skip(1).map(Impression.mapToItem).collect(Collectors.toList());
        br.close();

        this.impressions = FXCollections.observableArrayList(impressions);
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
