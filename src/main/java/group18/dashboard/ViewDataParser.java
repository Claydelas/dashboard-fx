package group18.dashboard;

import group18.dashboard.model.Click;
import group18.dashboard.model.Impression;
import group18.dashboard.model.Interaction;
import javafx.scene.chart.XYChart;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ViewDataParser {
    // timeResolution parameter filled by Calendar.MONTH, Calendar.MINUTE, etc.
    // Fills all 'Number of X' metrics charts
    public static XYChart.Series<String, Number> getCumulativeTimeSeries(String dataName, int timeResolution, List<Date> times) {
        final Map<Date, Number> quantities = new HashMap<>();

        for (Date time : times) {
            final Date roundedTime = DateUtils.round(time, timeResolution);
            quantities.putIfAbsent(roundedTime, 0);
            quantities.computeIfPresent(roundedTime, (key, value) -> value.intValue() + 1);
        }

        return mapToSeries(dataName, quantities);
    }
    public static XYChart.Series<String, Number> getSeriesOf(String dataName, List<LocalDateTime> times) {
        final Map<String, Number> quantities = new HashMap<>();

        for (LocalDateTime time : times) {
            String roundedTime = DateTimeFormatter.ISO_DATE.format(time);
            quantities.putIfAbsent(roundedTime, 0);
            quantities.computeIfPresent(roundedTime, (key, value) -> value.intValue() + 1);
        }

        return mapToSeries2(dataName, quantities);
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
    public static XYChart.Series<String, Number> getCPMTimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<Date, Number> cpms = new HashMap<>();
        final Map<Date, Number> cpmDatesNumber = new HashMap<>();
        final Map<Date, Number> cpmDatesCost = new HashMap<>();

        final List<Impression> sortedImpressions = impressions
                .stream()
                .sorted(Comparator.comparing(Impression::getDate))
                .limit(impressions.size() - impressions.size() % 1000) // Throw away last n < 1000 impressions
                .collect(Collectors.toList());

        for (int i = 0, length = sortedImpressions.size(); i < length; i += 1000) {
            double cpm = sortedImpressions
                    .stream()
                    .skip(i)
                    .limit(1000)
                    .mapToDouble(Impression::getCost)
                    .sum();

            final Impression firstImpression = sortedImpressions.get(i);
            final Impression lastImpression = sortedImpressions.get(i + 999);

            cpm += clicks
                    .parallelStream()
                    .filter(click ->
                            click.getDate().after(firstImpression.getDate()) &&
                                    click.getDate().before(lastImpression.getDate()))
                    .mapToDouble(Click::getCost)
                    .sum();

            final Date roundedDate = DateUtils.round(sortedImpressions.get(i + 999).getDate(), timeResolution);
            cpmDatesNumber.putIfAbsent(roundedDate, 0);
            cpmDatesNumber.computeIfPresent(roundedDate, (k, v) -> v.intValue() + 1);
            cpmDatesCost.putIfAbsent(roundedDate, 0);
            double finalCpm = cpm; // Needed for lambda expression
            cpmDatesCost.computeIfPresent(roundedDate, (k, v) -> v.doubleValue() + finalCpm);
        }

        for (Map.Entry<Date, Number> entry : cpmDatesCost.entrySet()) {
            cpms.put(entry.getKey(),
                    entry.getValue().doubleValue() / cpmDatesNumber.get(entry.getKey()).intValue());
        }

        return mapToSeries("Cost-per-mille", cpms);
    }

    public static double getCPM(List<Impression> impressions, List<Click> clicks) {
        return getTotalCost(impressions, clicks) / (impressions.size() * 1000);
    }

    public static XYChart.Series<String, Number> getTotalCostSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<Date, Number> totalCosts = new HashMap<>();

        for (Impression impression : impressions) {
            final Date roundedTime = DateUtils.round(impression.getDate(), timeResolution);
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val.doubleValue() + impression.getCost());
        }

        for (Click click : clicks) {
            final Date roundedTime = DateUtils.round(click.getDate(), timeResolution);
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val.doubleValue() + click.getCost());
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

    public static XYChart.Series<String, Number> getCTRTimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<Date, Integer> totalImpressions = new HashMap<>();
        final Map<Date, Integer> totalClicks = new HashMap<>();

        for (Impression impression : impressions) {
            final Date roundedTime = DateUtils.round(impression.getDate(), timeResolution);
            totalImpressions.putIfAbsent(roundedTime, 0);
            totalImpressions.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        for (Click click : clicks) {
            final Date roundedTime = DateUtils.round(click.getDate(), timeResolution);
            totalClicks.putIfAbsent(roundedTime, 0);
            totalClicks.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        final Map<Date, Number> ctrs = new HashMap<>();
        for (Map.Entry<Date, Integer> entry : totalImpressions.entrySet()) {
            ctrs.put(entry.getKey(), (double) totalClicks.get(entry.getKey()) / entry.getValue());
        }

        return mapToSeries("Click-through-rate", ctrs);
    }

    public static double getCTR(List<Impression> impressions, List<Click> clicks) {
        return (double) clicks.size() / impressions.size();
    }

    public static XYChart.Series<String, Number> getCPATimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        final Map<Date, Number> cpas = new HashMap<>();
        final List<Date> clickSeenDates = new ArrayList<>();
        final List<Date> impressionSeenDates = new ArrayList<>();
        final List<Date> interactionSeenDates = new ArrayList<>();

        final Map<Date, Number> distinctClicksCosts = new HashMap<>();
        final Map<Date, Number> distinctImpressionCosts = new HashMap<>();
        final Map<Date, Number> acquisitionsAtDate = new HashMap<>();

        for (Click click : clicks) {
            final Date roundedDate = DateUtils.round(click.getDate(), timeResolution);
            if (!clickSeenDates.contains(roundedDate)) {
                distinctClicksCosts.putIfAbsent(roundedDate, click.getCost());
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + click.getCost()); // Not sure this line ever runs
                clickSeenDates.add(roundedDate);
            } else {
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + click.getCost());
            }
        }

        for (Impression impression : impressions) {
            final Date roundedDate = DateUtils.round(impression.getDate(), timeResolution);
            if (!impressionSeenDates.contains(roundedDate)) {
                distinctImpressionCosts.putIfAbsent(roundedDate, impression.getCost());
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + impression.getCost()); // Not sure this line ever runs
                impressionSeenDates.add(roundedDate);
            } else {
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + impression.getCost());
            }
        }

        final List<Interaction> conversions = interactions
                .parallelStream()
                .filter(Interaction::isConversion)
                .collect(Collectors.toList());

        for (Interaction conversion : conversions) {
            final Date roundedDate = DateUtils.round(conversion.getEntryDate(), timeResolution);
            if (!interactionSeenDates.contains(roundedDate)) {
                acquisitionsAtDate.putIfAbsent(roundedDate, 0);
                acquisitionsAtDate.computeIfPresent(roundedDate, (key, d) -> d.intValue() + 1); // Not sure this line ever runs
                interactionSeenDates.add(roundedDate);
            } else {
                acquisitionsAtDate.computeIfPresent(roundedDate, (key, d) -> d.intValue() + 1);
            }
        }

        for (Map.Entry<Date, Number> acquisitions : acquisitionsAtDate.entrySet()) {
            double cost = 0;

            cost += distinctClicksCosts.get(acquisitions.getKey()).doubleValue();
            cost += distinctImpressionCosts.get(acquisitions.getKey()).doubleValue();

            cpas.put(acquisitions.getKey(), cost / acquisitions.getValue().doubleValue());
        }

        return mapToSeries("Cost-per-acquisition", cpas);
    }

    public static double getCPA(List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        return getTotalCost(impressions, clicks) / getConversions(interactions);
    }

    public static XYChart.Series<String, Number> getCPCTimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<Date, Number> cpcs = new HashMap<>();
        final List<Date> clickSeenDates = new ArrayList<>();
        final List<Date> impressionSeenDates = new ArrayList<>();

        final Map<Date, Number> distinctClicksCosts = new HashMap<>();
        final Map<Date, Number> distinctImpressionCosts = new HashMap<>();
        final Map<Date, Number> clicksAtDate = new HashMap<>();

        for (Click click : clicks) {
            final Date roundedDate = DateUtils.round(click.getDate(), timeResolution);
            if (!clickSeenDates.contains(roundedDate)) {
                distinctClicksCosts.putIfAbsent(roundedDate, click.getCost());
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + click.getCost()); // Not sure this line ever runs
                clicksAtDate.putIfAbsent(roundedDate, 0);
                clicksAtDate.computeIfPresent(roundedDate, (key, d) -> d.intValue() + 1); // Not sure this line ever runs
                clickSeenDates.add(roundedDate);
            } else {
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + click.getCost());
                clicksAtDate.computeIfPresent(roundedDate, (key, d) -> d.intValue() + 1);
            }
        }

        for (Impression impression : impressions) {
            final Date roundedDate = DateUtils.round(impression.getDate(), timeResolution);
            if (!impressionSeenDates.contains(roundedDate)) {
                distinctImpressionCosts.putIfAbsent(roundedDate, impression.getCost());
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + impression.getCost()); // Not sure this line ever runs
                impressionSeenDates.add(roundedDate);
            } else {
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + impression.getCost());
            }
        }

        for (Map.Entry<Date, Number> click : clicksAtDate.entrySet()) {
            double cost = 0;

            cost += distinctClicksCosts.get(click.getKey()).doubleValue();
            cost += distinctImpressionCosts.get(click.getKey()).doubleValue();

            cpcs.put(click.getKey(), cost / click.getValue().doubleValue());
        }

        return mapToSeries("Cost-per-click", cpcs);
    }

    public static double getCPC(List<Impression> impressions, List<Click> clicks) {
        return getTotalCost(impressions, clicks) / clicks.size();
    }

    // TODO
    public static XYChart.Series<String, Number> getBounceRateTimeSeries(int timeResolution, List<Click> clicks, List<Interaction> interactions) {
        final Map<Date, Integer> totalClicks = new HashMap<>();
        final Map<Date, Integer> totalInteractions = new HashMap<>();

        for (Click click : clicks) {
            final Date roundedTime = DateUtils.round(click.getDate(), timeResolution);
            totalClicks.putIfAbsent(roundedTime, 0);
            totalClicks.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        for (Interaction interaction : interactions) {
            final Date roundedTime = DateUtils.round(interaction.getEntryDate(), timeResolution);
            totalInteractions.putIfAbsent(roundedTime, 0);
            if (interaction.isConversion()) {
                totalInteractions.computeIfPresent(roundedTime, (key, val) -> val + 1);
            }
        }

        final Map<Date, Number> bounceRates = new HashMap<>();
        for (Map.Entry<Date, Integer> entry : totalClicks.entrySet()) {
            bounceRates.put(entry.getKey(), (double) totalInteractions.get(entry.getKey()) / entry.getValue());
        }

        return mapToSeries("Bounce rate", bounceRates);
    }

    public static double getBounceRate(List<Click> clicks, List<Interaction> interactions) {
        return (double) getBounces(interactions) / clicks.size();
    }

    private static <U> XYChart.Series<String, U> mapToSeries(String seriesName, Map<Date, U> map) {
        final XYChart.Series<String, U> series = new XYChart.Series<>();
        series.setName(seriesName);

        final List<Map.Entry<Date, U>> orderedEntries = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (Map.Entry<Date, U> entry : orderedEntries) {
            series.getData().add(new XYChart.Data<>(dateToString(entry.getKey()), entry.getValue()));
        }

        return series;
    }
    private static <U> XYChart.Series<String, U> mapToSeries2(String seriesName, Map<String, U> map) {
        final XYChart.Series<String, U> series = new XYChart.Series<>();
        series.setName(seriesName);

        final List<Map.Entry<String, U>> orderedEntries = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (Map.Entry<String, U> entry : orderedEntries) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        return series;
    }
}