package group18.dashboard;

import group18.dashboard.model.Click;
import group18.dashboard.model.Impression;
import group18.dashboard.model.Interaction;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.stream.Collectors;

public class ViewDataParser {
    // timeResolution parameter filled by Calendar.MONTH, Calendar.MINUTE, etc.
    // Fills all 'Number of X' metrics charts
    public static XYChart.Series<String, Number> getCumulativeTimeSeries(String dataName, int timeResolution, List<Date> times) {
        final Map<String, Number> quantities = new HashMap<>();

        for (Date time : times) {
            final String roundedTime = dateToString(DateUtils.round(time, timeResolution));
            quantities.putIfAbsent(roundedTime, 0);
            quantities.computeIfPresent(roundedTime, (key, value) -> value.intValue() + 1);
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
    public static XYChart.Series<String, Number> getCPMTimeSeries(List<Impression> impressions, List<Click> clicks) {
        final Map<String, Number> cpms = new HashMap<>();

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

    public static XYChart.Series<String, Number> getTotalCostSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<String, Number> totalCosts = new HashMap<>();

        for (Impression impression : impressions) {
            final String roundedTime = dateToString(DateUtils.round(impression.getDate(), timeResolution));
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val.doubleValue() + impression.getCost());
        }

        for (Click click : clicks) {
            final String roundedTime = dateToString(DateUtils.round(click.getDate(), timeResolution));
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

        final Map<String, Number> ctrs = new HashMap<>();
        for (Map.Entry<String, Integer> entry : totalImpressions.entrySet()) {
            ctrs.put(entry.getKey(), (double) totalClicks.get(entry.getKey()) / entry.getValue());
        }

        return mapToSeries("Click-through-rate", ctrs);
    }

    public static double getCTR(List<Impression> impressions, List<Click> clicks) {
        return (double) clicks.size() / impressions.size();
    }

    public static XYChart.Series<String, Number> getCPATimeSeries(List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        final Map<String, Number> cpas = new HashMap<>();

        final List<Interaction> conversions = interactions
                .parallelStream()
                .filter(Interaction::isConversion)
                .collect(Collectors.toList());

        for (int i = 1, length = conversions.size(); i < length; i++) {
            final Interaction previousConversion = conversions.get(i - 1);
            final Interaction currentConversion = conversions.get(i);

            if (currentConversion.getExitDate() == null || previousConversion.getExitDate() == null) {
                continue;
            }

            double cost = 0;

            cost += clicks
                    .parallelStream()
                    .filter(click ->
                            click.getDate().after(previousConversion.getExitDate()) &&
                                    click.getDate().before(currentConversion.getExitDate()))
                    .mapToDouble(Click::getCost)
                    .sum();

            cost += impressions
                    .parallelStream()
                    .filter(impression ->
                            impression.getDate().after(previousConversion.getExitDate()) &&
                                    impression.getDate().before(currentConversion.getExitDate()))
                    .mapToDouble(Impression::getCost)
                    .sum();

            cpas.put(dateToString(currentConversion.getExitDate()), cost);
        }

        return mapToSeries("Cost-per-acquisition", cpas);
    }

    public static double getCPA(List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        return getTotalCost(impressions, clicks) / getConversions(interactions);
    }

    public static XYChart.Series<String, Number> getCPCTimeSeries(int timeResolution, List<Impression> impressions, List<Click> clicks) {
        final Map<String, Number> cpcs = new HashMap<>();
        final List<Date> seenDates = new ArrayList<>();

        final Map<Click, Number> distinctClicksCosts = new HashMap<>();

        for (Click click : clicks) {
            final Date roundedDate = DateUtils.round(click.getDate(), timeResolution);
            if (!seenDates.contains(roundedDate)) {
                distinctClicksCosts.putIfAbsent(click, click.getCost());
                distinctClicksCosts.computeIfPresent(click, (key, d) -> d.doubleValue() + click.getCost()); // Not sure this line ever runs
                seenDates.add(roundedDate);
            }
            distinctClicksCosts.computeIfPresent(click, (key, d) -> d.doubleValue() + click.getCost()); // Not sure this line ever runs
        }

        final List<Click> sortedClicks = distinctClicksCosts.keySet()
                .stream()
                .sorted(Comparator.comparing(Click::getDate))
                .collect(Collectors.toList());

        final List<Impression> sortedImpressions = impressions
                .stream()
                .sorted(Comparator.comparing(Impression::getDate))
                .collect(Collectors.toList());

        for (final Click currentClick : sortedClicks) {
            double cost = 0;

            cost += distinctClicksCosts.get(currentClick).doubleValue();

            Iterator<Impression> it = sortedImpressions.iterator();

            while (it.hasNext()) {
                Impression impression = it.next();
                if (impression.getDate().before(currentClick.getDate())) {
                    cost += impression.getCost();
                    it.remove();
                } else {
                    break;
                }
            }

            cpcs.put(dateToString(currentClick.getDate()), cost);
        }

        return mapToSeries("Cost-per-click", cpcs);
    }

    public static double getCPC(List<Impression> impressions, List<Click> clicks) {
        return getTotalCost(impressions, clicks) / clicks.size();
    }

    // TODO
    public static XYChart.Series<String, Number> getBounceRateTimeSeries(int timeResolution, List<Click> clicks, List<Interaction> interactions) {
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

        final Map<String, Number> bounceRates = new HashMap<>();
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
