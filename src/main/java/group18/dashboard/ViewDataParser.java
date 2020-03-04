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
                .limit(impressions.size() - impressions.size() % 1000) // Throw away last n < 1000 impressions
                .collect(Collectors.toList());
        // Maybe use this if searching through all clicks in the loop is too slow
//        final List<Click> sortedClicks = clicks
//                .stream()
//                .sorted(Comparator.comparing(Click::getDate))
//                .collect(Collectors.toList());

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

    public static XYChart.Series<String, Double> getCPATimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        final Map<String, Double> cpas = new HashMap<>();
        final List<Date> clickSeenDates = new ArrayList<>();
        final List<Date> impressionSeenDates = new ArrayList<>();
        final List<Date> interactionSeenDates = new ArrayList<>();

        final Map<Date, Double> distinctClicksCosts = new HashMap<>();
        final Map<Date, Double> distinctImpressionCosts = new HashMap<>();
        final Map<Date, Integer> acquisitionsAtDate = new HashMap<>();

        for (Click click : clicks) {
            final Date roundedDate = DateUtils.round(click.getDate(), timeResolution);
            if (!clickSeenDates.contains(roundedDate)) {
                distinctClicksCosts.putIfAbsent(roundedDate, click.getCost());
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d + click.getCost()); // Not sure this line ever runs
                clickSeenDates.add(roundedDate);
            } else {
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d + click.getCost());
            }
        }

        for (Impression impression : impressions) {
            final Date roundedDate = DateUtils.round(impression.getDate(), timeResolution);
            if (!impressionSeenDates.contains(roundedDate)) {
                distinctImpressionCosts.putIfAbsent(roundedDate, impression.getCost());
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d + impression.getCost()); // Not sure this line ever runs
                impressionSeenDates.add(roundedDate);
            } else {
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d + impression.getCost());
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
                acquisitionsAtDate.computeIfPresent(roundedDate, (key, d) -> d + 1); // Not sure this line ever runs
                interactionSeenDates.add(roundedDate);
            } else {
                acquisitionsAtDate.computeIfPresent(roundedDate, (key, d) -> d + 1);
            }
        }

        for (Map.Entry<Date, Integer> acquisitions : acquisitionsAtDate.entrySet()) {
            double cost = 0;

            cost += distinctClicksCosts.get(acquisitions.getKey());
            cost += distinctImpressionCosts.get(acquisitions.getKey());

            cpas.put(dateToString(acquisitions.getKey()), cost / acquisitions.getValue());
        }

        return mapToSeries("Cost-per-acquisition", cpas);
    }

    public static double getCPA(List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        return getTotalCost(impressions, clicks) / getConversions(interactions);
    }

    public static XYChart.Series<String, Double> getCPCTimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<String, Double> cpcs = new HashMap<>();
        final List<Date> clickSeenDates = new ArrayList<>();
        final List<Date> impressionSeenDates = new ArrayList<>();

        final Map<Date, Double> distinctClicksCosts = new HashMap<>();
        final Map<Date, Double> distinctImpressionCosts = new HashMap<>();
        final Map<Date, Integer> clicksAtDate = new HashMap<>();

        for (Click click : clicks) {
            final Date roundedDate = DateUtils.round(click.getDate(), timeResolution);
            if (!clickSeenDates.contains(roundedDate)) {
                distinctClicksCosts.putIfAbsent(roundedDate, click.getCost());
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d + click.getCost()); // Not sure this line ever runs
                clicksAtDate.putIfAbsent(roundedDate, 0);
                clicksAtDate.computeIfPresent(roundedDate, (key, d) -> d + 1); // Not sure this line ever runs
                clickSeenDates.add(roundedDate);
            } else {
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d + click.getCost());
                clicksAtDate.computeIfPresent(roundedDate, (key, d) -> d + 1);
            }
        }

        for (Impression impression : impressions) {
            final Date roundedDate = DateUtils.round(impression.getDate(), timeResolution);
            if (!impressionSeenDates.contains(roundedDate)) {
                distinctImpressionCosts.putIfAbsent(roundedDate, impression.getCost());
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d + impression.getCost()); // Not sure this line ever runs
                impressionSeenDates.add(roundedDate);
            } else {
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d + impression.getCost());
            }
        }

        for (Map.Entry<Date, Integer> click : clicksAtDate.entrySet()) {
            double cost = 0;

            cost += distinctClicksCosts.get(click.getKey());
            cost += distinctImpressionCosts.get(click.getKey());

            cpcs.put(dateToString(click.getKey()), cost / click.getValue());
        }

        return mapToSeries("Cost-per-click", cpcs);
    }

    public static double getCPC(List<Impression> impressions, List<Click> clicks) {
        return getTotalCost(impressions, clicks) / clicks.size();
    }

    // TODO
    public static XYChart.Series<String, Double> getBounceRateTimeSeries(int timeResolution, List<Click> clicks, List<Interaction> interactions) {
        final Map<String, Integer> totalClicks = new HashMap<>();
        final Map<String, Integer> totalInteractions = new HashMap<>();

        for (Click click : clicks) {
            final String roundedTime = dateToString(DateUtils.round(click.getDate(), timeResolution));
            totalClicks.putIfAbsent(roundedTime, 0);
            totalClicks.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        for (Interaction interaction : interactions) {
            final String roundedTime = dateToString(DateUtils.round(interaction.getEntryDate(), timeResolution));
            totalInteractions.putIfAbsent(roundedTime, 0);
            if (interaction.isConversion()) {
                totalInteractions.computeIfPresent(roundedTime, (key, val) -> val + 1);
            }
        }

        final Map<String, Double> bounceRates = new HashMap<>();
        for (Map.Entry<String, Integer> entry : totalClicks.entrySet()) {
            bounceRates.put(entry.getKey(), (double) totalInteractions.get(entry.getKey()) / entry.getValue());
        }

        return mapToSeries("Bounce rate", bounceRates);
    }

    public static double getBounceRate(List<Click> clicks, List<Interaction> interactions) {
        return (double) getBounces(interactions) / clicks.size();
    }

    private static <T, U> XYChart.Series<T, U> mapToSeries(String seriesName, Map<T, U> map) {
        final XYChart.Series<T, U> series = new XYChart.Series<>();
        series.setName(seriesName);

        for (Map.Entry<T, U> entry : map.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        return series;
    }
}
