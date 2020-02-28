package group18.dashboard;

import org.apache.commons.lang.time.DateUtils;

import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.stream.Collectors;

public class ViewDataParser {
    // timeResolution parameter filled by Calendar.MONTH, Calendar.MINUTE, etc.
    // Fills all 'Number of X' metrics charts
    public static XYChart.Series<Date, Integer> getCumulativeTimeSeries(String dataName, int timeResolution, List<Date> times) {
        final Map<Date, Integer> quantities = new HashMap<>();

        for (Date time : times) {
            final Date roundedTime = DateUtils.round(time, timeResolution);
            quantities.putIfAbsent(roundedTime, 0);
            quantities.computeIfPresent(roundedTime, (key, value) -> value + 1);
        }

        return mapToSeries(dataName, quantities);
    }

    // Every 1000 impressions calculate the cost sum and plot them
    public static XYChart.Series<String, Double> getCPMTimeSeries(List<Impression> impressions) {
        final Map<String, Double> cpms = new HashMap<>();

        final List<Impression> sortedImpressions = impressions
                .stream()
                .sorted(Comparator.comparing(Impression::getDate))
                .collect(Collectors.toList());

        for (int i = 0, length = sortedImpressions.size(); i < length; i += 1000) {
            final double cpm = sortedImpressions
                    .stream()
                    .skip(i * 1000)
                    .limit(1000)
                    .mapToDouble(Impression::getCost)
                    .sum();
            cpms.put(sortedImpressions.get(i + 999).getDate().toString(), cpm);
        }

        return mapToSeries("Cost-per-thousand impressions", cpms);
    }

    public static XYChart.Series<Date, Double> getTotalCostSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<Date, Double> totalCosts = new HashMap<>();

        for (Impression impression : impressions) {
            final Date roundedTime = DateUtils.round(impression.getDate(), timeResolution);
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val + impression.getCost());
        }

        for (Click click : clicks) {
            final Date roundedTime = DateUtils.round(click.getDate(), timeResolution);
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val + click.getCost());
        }

        return mapToSeries("Total cost", totalCosts);
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
