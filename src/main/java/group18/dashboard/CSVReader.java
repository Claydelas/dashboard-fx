package group18.dashboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import group18.dashboard.ViewDataParser;

public class CSVReader {
    //Ideally one CSVReader object should be used for each campaign
    //Getters are provided for each log
    private ArrayList<Impression> impressions = new ArrayList<>();
    private ArrayList<Click> clicks = new ArrayList<>();
    private ArrayList<Interaction> interactions = new ArrayList<>();

    public CSVReader(String filepath) throws Exception{
        // Takes in the filepath of the campaign folder and
        // generates the objects for each item

        //Impressions
        File impressionsFile = new File(filepath+File.separator+"impression_log.csv");
        BufferedReader brImpressions = new BufferedReader(new FileReader(impressionsFile));
        String line = "";
        //First line is the column headings so should be ignored
        line = brImpressions.readLine();
        while((line = brImpressions.readLine()) != null){
            impressions.add(new Impression((line)));
        }

        //Clicks
        File clicksFile = new File(filepath+File.separator+"click_log.csv");
        BufferedReader brClicks = new BufferedReader(new FileReader(clicksFile));
        line = "";
        //First line is the column headings so should be ignored
        line = brClicks.readLine();
        while((line = brClicks.readLine()) != null){
            clicks.add(new Click((line)));
        }

        //Servers
        File serversFile = new File(filepath+File.separator+"server_log.csv");
        BufferedReader brServers = new BufferedReader(new FileReader(serversFile));
        line = "";
        //First line is the column headings so should be ignored
        line = brServers.readLine();
        while((line = brServers.readLine()) != null){
            interactions.add(new Interaction((line)));
        }
    }
    public static void main(String[] args) throws Exception{
        //Example for getting all objects from a given campaign directory
        benchmarks();
    }

    private static void benchmarks() throws Exception {
        System.out.println("----- TIMINGS ------\n");

        long t = System.currentTimeMillis();
        CSVReader c = new CSVReader("/home/platelminto/Documents/UG soton/Year 2/COMP2211 - Software Engineering Group Project/2_week_campaign_2");
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

    public ArrayList<Impression> getImpressions() {
        return impressions;
    }

    public ArrayList<Click> getClicks() {
        return clicks;
    }

    public ArrayList<Interaction> getInteractions() {
        return interactions;
    }
}
