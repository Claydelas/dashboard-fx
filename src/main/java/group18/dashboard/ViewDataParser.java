package group18.dashboard;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.stream.Collectors;

public class ViewDataParser {
    // timeResolution parameter filled by Calendar.MONTH, Calendar.MINUTE, etc.
    // Fills all 'Number of X' metrics charts
    public static XYChart.Series<String, Integer> getCumulativeTimeSeries(String dataName, int timeResolution, List<Date> times) {
        final Map<String, Integer> quantities = new HashMap<>();

        for (Date time : times) {
            final String roundedTime = dateToString(DateUtils.round(time, timeResolution));
            quantities.putIfAbsent(roundedTime, 0);
            quantities.computeIfPresent(roundedTime, (key, value) -> value + 1);
        }

        return mapToSeries(dataName, quantities);
    }

    private static String dateToString(Date date) {
        return DateFormatUtils.format(date, "yyy-MM-dd HH:mm:ss");
    }

    public static long getUniques(List<Click> clicks) {
        return clicks
                .parallelStream()
                .map(Click::getID)
                .distinct()
                .count();
    }

    public static long getBounces(List<Interaction> interactions) {
        return interactions
                .parallelStream()
                .filter(interaction -> !interaction.isConversion())
                .count();
    }

    public static long getConversions(List<Interaction> interactions) {
        return interactions
                .parallelStream()
                .filter(Interaction::isConversion)
                .count();
    }

    // Every 1000 impressions calculate the (impressions + click) cost sum and plot them
    public static XYChart.Series<String, Double> getCPMTimeSeries(List<Impression> impressions, List<Click> clicks) {
        final Map<String, Double> cpms = new HashMap<>();

        final List<Impression> sortedImpressions = impressions
                .stream()
                .sorted(Comparator.comparing(Impression::getDate))
                .collect(Collectors.toList());
        // Maybe use this if searching through all clicks in the loop is too slow
//        final List<Click> sortedClicks = clicks
//                .stream()
//                .sorted(Comparator.comparing(Click::getDate))
//                .collect(Collectors.toList());

        for (int i = 0, length = sortedImpressions.size(); i < length; i += 1000) {
            double cpm = sortedImpressions
                    .stream()
                    .skip(i * 1000)
                    .limit(1000)
                    .mapToDouble(Impression::getCost)
                    .sum();

            final Impression firstImpression = sortedImpressions.get(i * 1000);
            final Impression lastImpression = sortedImpressions.get(i * 1000 + 999);

            cpm += clicks
                    .parallelStream()
                    .filter(click ->
                            click.getDate().after(firstImpression.getDate()) &&
                            click.getDate().before(lastImpression.getDate()))
                    .mapToDouble(Click::getCost)
                    .sum();

            cpms.put(dateToString(sortedImpressions.get(i + 999).getDate()), cpm);
        }

        return mapToSeries("Cost-per-thousand impressions", cpms);
    }

    public static double getCPM(List<Impression> impressions, List<Click> clicks) {
        return getTotalCost(impressions, clicks) / (impressions.size() * 1000);
    }

    public static XYChart.Series<String, Double> getTotalCostSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<String, Double> totalCosts = new HashMap<>();

        for (Impression impression : impressions) {
            final String roundedTime = dateToString(DateUtils.round(impression.getDate(), timeResolution));
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val + impression.getCost());
        }

        for (Click click : clicks) {
            final String roundedTime = dateToString(DateUtils.round(click.getDate(), timeResolution));
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val + click.getCost());
        }

        return mapToSeries("Total cost", totalCosts);
    }

    public static double getTotalCost(List<Impression> impressions, List<Click> clicks) {
        double cost = 0;

        cost += impressions
                .parallelStream()
                .mapToDouble(Impression::getCost)
                .sum();
        cost += clicks
                .parallelStream()
                .mapToDouble(Click::getCost)
                .sum();

        return cost;
    }

    public static XYChart.Series<String, Double> getCTRTimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<String, Integer> totalImpressions = new HashMap<>();
        final Map<String, Integer> totalClicks = new HashMap<>();

        for (Impression impression : impressions) {
            final String roundedTime = dateToString(DateUtils.round(impression.getDate(), timeResolution));
            totalImpressions.putIfAbsent(roundedTime, 0);
            totalImpressions.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        for (Click click : clicks) {
            final String roundedTime = dateToString(DateUtils.round(click.getDate(), timeResolution));
            totalClicks.putIfAbsent(roundedTime, 0);
            totalClicks.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        final Map<String, Double> ctrs = new HashMap<>();
        for (Map.Entry<String, Integer> entry : totalImpressions.entrySet()) {
            ctrs.put(entry.getKey(), (double) totalClicks.get(entry.getKey()) / entry.getValue());
        }

        return mapToSeries("Click-through-rate", ctrs);
    }

    public static double getCTR(List<Impression> impressions, List<Click> clicks) {
        return (double) clicks.size() / impressions.size();
    }

    // TODO
    public static XYChart.Series<String, Double> getCPATimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        return null;
    }

    public static double getCPA(List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        return getTotalCost(impressions, clicks) / getConversions(interactions);
    }

    // TODO
    public static XYChart.Series<String, Double> getCPCTimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        return null;
    }

    public static double getCPC(List<Impression> impressions, List<Click> clicks) {
        return getTotalCost(impressions, clicks) / clicks.size();
    }

    // TODO
    public static XYChart.Series<String, Double> getBounceRateTimeSeries(int timeResolution, List<Click> clicks, List<Interaction> interactions) {
        return null;
    }

    public static double getBounceRate(List<Click> clicks, List<Interaction> interactions) {
        return (double) getBounces(interactions) / clicks.size();
    }

    private static <T, U> XYChart.Series<T, U> mapToSeries(String seriesName, Map<T, U> map) {
        final XYChart.Series<T, U> series = new XYChart.Series<>();
        series.setName(seriesName);

        for (Map.Entry<T, U> entry: map.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        return series;
    }
}
