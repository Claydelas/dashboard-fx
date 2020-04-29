package group18.dashboard;

import group18.dashboard.database.tables.records.ClickRecord;
import group18.dashboard.database.tables.records.ImpressionRecord;
import group18.dashboard.database.tables.records.InteractionRecord;
import javafx.scene.chart.XYChart;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewDataParser {
    private static final DateTimeFormatter hourlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:00");
    private static final DateTimeFormatter dailyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter weeklyFormatter = DateTimeFormatter.ofPattern("yyyy LLLL, 'Week' w");

    private static XYChart.Series<String, Number> getClickCostsHistogram(String divisionName, boolean perClick, List<ClickRecord> clicks, Function<ClickRecord, Integer> getClickTime, int timeDivisions, Function<Integer, String> showDivision) {
        int[] clicksPerTime = new int[timeDivisions + 1];
        double[] costsPerTime = new double[timeDivisions + 1];

        for (ClickRecord click : clicks) {
            final int time = getClickTime.apply(click);

            clicksPerTime[time] += 1;
            costsPerTime[time] += click.getCost();
        }

        XYChart.Series<String, Number> clickCosts = new XYChart.Series<>();
        clickCosts.setName(divisionName);
        int index;

        if (timeDivisions == 23) index = 0;
        else index = 1;

        for (int i = index; i <= timeDivisions; i++) {
            clickCosts.getData().add(new XYChart.Data<>(
                    showDivision.apply(i), costsPerTime[i] / (perClick ? clicksPerTime[i] : 1)
            ));
        }

        return clickCosts;
    }

    public static XYChart.Series<String, Number> getDailyClickCostsHistogram(String addedInfo, List<ClickRecord> clicks, boolean perClick) {
        return getClickCostsHistogram(
                "Day of Week" + addedInfo,
                perClick,
                clicks,
                c -> c.getDate().getDayOfWeek().getValue(),
                7,
                d -> DayOfWeek.of(d).getDisplayName(TextStyle.FULL, Locale.getDefault())
        );
    }

    public static XYChart.Series<String, Number> getHourlyClickCostsHistogram(String addedInfo, List<ClickRecord> clicks, boolean perClick) {
        return getClickCostsHistogram(
                "Hour of Day" + addedInfo,
                perClick,
                clicks,
                c -> c.getDate().getHour(),
                23,
                Object::toString
        );
    }

    private static List<XYChart.Series<Integer, Double>> getFilteredClickCostsHistogram() {
        // TODO, will likely have to make a db merging impression and click data.
        return null;
    }

    private static String roundDate(LocalDateTime dateTime, TimeGranularity granularity) {
        String roundedDate = null;

        switch (granularity) {
            case HOURLY:
                roundedDate = hourlyFormatter.format(dateTime);
                break;
            case DAILY:
                roundedDate = dailyFormatter.format(dateTime);
                break;
            case WEEKLY:
                roundedDate = weeklyFormatter.format(dateTime);
                break;
        }

        return roundedDate;
    }

    public static XYChart.Series<String, Number> getSeriesOf(String dataName, TimeGranularity granularity, List<LocalDateTime> times) {
        final Map<String, Number> quantities = new HashMap<>();

        for (LocalDateTime time : times) {
            String roundedTime = roundDate(time, granularity);
            quantities.putIfAbsent(roundedTime, 0);
            quantities.computeIfPresent(roundedTime, (key, value) -> value.intValue() + 1);
        }

        return mapToSeries(dataName, quantities);
    }

//    private static String dateToString(Date date) {
//        return DateFormatUtils.format(date, "yyy-MM-dd HH:mm:ss");
//    }

    public static long getUniques(List<ClickRecord> clicks) {
        return clicks
                .parallelStream()
                .map(ClickRecord::getUser)
                .distinct()
                .count();
    }

    public static long getBounces(List<InteractionRecord> interactions) {
        return interactions
                .parallelStream()
                .filter(interaction -> !interaction.getConversion())
                .count();
    }

    public static long getConversions(List<InteractionRecord> interactions) {
        return interactions
                .parallelStream()
                .filter(InteractionRecord::getConversion)
                .count();
    }

    // Every 1000 impressions calculate the (impressions + click) cost sum and plot them
    public static XYChart.Series<String, Number> getCPMTimeSeries(String addedInfo, TimeGranularity granularity, List<ImpressionRecord> impressions, List<ClickRecord> clicks) {
        final Map<String, Number> cpms = new HashMap<>();
        final Map<String, Number> cpmDatesNumber = new HashMap<>();
        final Map<String, Number> cpmDatesCost = new HashMap<>();

        final List<ImpressionRecord> sortedImpressions = impressions
                .stream()
                .sorted(Comparator.comparing(ImpressionRecord::getDate))
                .limit(impressions.size() - impressions.size() % 1000) // Throw away last n < 1000 impressions
                .collect(Collectors.toList());

        for (int i = 0, length = sortedImpressions.size(); i < length; i += 1000) {
            double cpm = sortedImpressions
                    .stream()
                    .skip(i)
                    .limit(1000)
                    .mapToDouble(ImpressionRecord::getCost)
                    .sum();

            final ImpressionRecord firstImpression = sortedImpressions.get(i);
            final ImpressionRecord lastImpression = sortedImpressions.get(i + 999);

            cpm += clicks
                    .parallelStream()
                    .filter(click ->
                            click.getDate().isAfter(firstImpression.getDate()) &&
                                    click.getDate().isBefore(lastImpression.getDate()))
                    .mapToDouble(ClickRecord::getCost)
                    .sum();

            final String roundedDate = roundDate(sortedImpressions.get(i + 999).getDate(), granularity);
            cpmDatesNumber.putIfAbsent(roundedDate, 0);
            cpmDatesNumber.computeIfPresent(roundedDate, (k, v) -> v.intValue() + 1);
            cpmDatesCost.putIfAbsent(roundedDate, 0);
            double finalCpm = cpm; // Needed for lambda expression
            cpmDatesCost.computeIfPresent(roundedDate, (k, v) -> v.doubleValue() + finalCpm);
        }

        for (Map.Entry<String, Number> entry : cpmDatesCost.entrySet()) {
            cpms.put(entry.getKey(),
                    entry.getValue().doubleValue() / cpmDatesNumber.get(entry.getKey()).intValue());
        }

        return mapToSeries("Cost-per-mille" + addedInfo, cpms);
    }

    public static double getCPM(List<ImpressionRecord> impressions, List<ClickRecord> clicks) {
        return getTotalCost(impressions, clicks) / (impressions.size() * 1000);
    }

    public static XYChart.Series<String, Number> getTotalCostSeries(String addedInfo, TimeGranularity granularity, List<ImpressionRecord> impressions, List<ClickRecord> clicks) {
        final Map<String, Number> totalCosts = new HashMap<>();

        for (ImpressionRecord impression : impressions) {
            final String roundedTime = roundDate(impression.getDate(), granularity);
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val.doubleValue() + impression.getCost());
        }

        for (ClickRecord click : clicks) {
            final String roundedTime = roundDate(click.getDate(), granularity);
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val.doubleValue() + click.getCost());
        }

        return mapToSeries("Total cost" + addedInfo, totalCosts);
    }

    public static double getTotalCost(List<ImpressionRecord> impressions, List<ClickRecord> clicks) {
        double cost = 0;

        cost += impressions
                .parallelStream()
                .mapToDouble(ImpressionRecord::getCost)
                .sum();
        cost += clicks
                .parallelStream()
                .mapToDouble(ClickRecord::getCost)
                .sum();

        return cost;
    }

    public static XYChart.Series<String, Number> getCTRTimeSeries(String addedInfo, TimeGranularity granularity, List<ImpressionRecord> impressions, List<ClickRecord> clicks) {
        final Map<String, Integer> totalImpressions = new HashMap<>();
        final Map<String, Integer> totalClicks = new HashMap<>();

        for (ImpressionRecord impression : impressions) {
            final String roundedTime = roundDate(impression.getDate(), granularity);
            totalImpressions.putIfAbsent(roundedTime, 0);
            totalImpressions.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        for (ClickRecord click : clicks) {
            final String roundedTime = roundDate(click.getDate(), granularity);
            totalClicks.putIfAbsent(roundedTime, 0);
            totalClicks.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        final Map<String, Number> ctrs = new HashMap<>();
        for (Map.Entry<String, Integer> entry : totalImpressions.entrySet()) {
            ctrs.put(entry.getKey(), (double) totalClicks.get(entry.getKey()) / entry.getValue());
        }

        return mapToSeries("Click-through-rate" + addedInfo, ctrs);
    }

    public static double getCTR(List<ImpressionRecord> impressions, List<ClickRecord> clicks) {
        return (double) clicks.size() / impressions.size();
    }

    public static XYChart.Series<String, Number> getCPATimeSeries(String addedInfo, TimeGranularity granularity, List<ImpressionRecord> impressions, List<ClickRecord> clicks, List<InteractionRecord> interactions) {
        final Map<String, Number> cpas = new HashMap<>();
        final List<String> clickSeenDates = new ArrayList<>();
        final List<String> impressionSeenDates = new ArrayList<>();
        final List<String> interactionSeenDates = new ArrayList<>();

        final Map<String, Number> distinctClicksCosts = new HashMap<>();
        final Map<String, Number> distinctImpressionCosts = new HashMap<>();
        final Map<String, Number> acquisitionsAtDate = new HashMap<>();

        for (ClickRecord click : clicks) {
            final String roundedDate = roundDate(click.getDate(), granularity);
            if (!clickSeenDates.contains(roundedDate)) {
                distinctClicksCosts.putIfAbsent(roundedDate, click.getCost());
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + click.getCost()); // Not sure this line ever runs
                clickSeenDates.add(roundedDate);
            } else {
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + click.getCost());
            }
        }

        for (ImpressionRecord impression : impressions) {
            final String roundedDate = roundDate(impression.getDate(), granularity);
            if (!impressionSeenDates.contains(roundedDate)) {
                distinctImpressionCosts.putIfAbsent(roundedDate, impression.getCost());
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + impression.getCost()); // Not sure this line ever runs
                impressionSeenDates.add(roundedDate);
            } else {
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + impression.getCost());
            }
        }

        final List<InteractionRecord> conversions = interactions
                .parallelStream()
                .filter(InteractionRecord::getConversion)
                .collect(Collectors.toList());

        for (InteractionRecord conversion : conversions) {
            final String roundedDate = roundDate(conversion.getEntryDate(), granularity);
            if (!interactionSeenDates.contains(roundedDate)) {
                acquisitionsAtDate.putIfAbsent(roundedDate, 0);
                acquisitionsAtDate.computeIfPresent(roundedDate, (key, d) -> d.intValue() + 1); // Not sure this line ever runs
                interactionSeenDates.add(roundedDate);
            } else {
                acquisitionsAtDate.computeIfPresent(roundedDate, (key, d) -> d.intValue() + 1);
            }
        }

        for (Map.Entry<String, Number> acquisitions : acquisitionsAtDate.entrySet()) {
            double cost = 0;

            cost += distinctClicksCosts.get(acquisitions.getKey()).doubleValue();
            cost += distinctImpressionCosts.get(acquisitions.getKey()).doubleValue();

            cpas.put(acquisitions.getKey(), cost / acquisitions.getValue().doubleValue());
        }

        return mapToSeries("Cost-per-acquisition" + addedInfo, cpas);
    }

    public static double getCPA(List<ImpressionRecord> impressions, List<ClickRecord> clicks, List<InteractionRecord> interactions) {
        return getTotalCost(impressions, clicks) / getConversions(interactions);
    }

    public static XYChart.Series<String, Number> getCPCTimeSeries(String addedInfo, TimeGranularity granularity, List<ImpressionRecord> impressions, List<ClickRecord> clicks) {
        final Map<String, Number> cpcs = new HashMap<>();
        final List<String> clickSeenDates = new ArrayList<>();
        final List<String> impressionSeenDates = new ArrayList<>();

        final Map<String, Number> distinctClicksCosts = new HashMap<>();
        final Map<String, Number> distinctImpressionCosts = new HashMap<>();
        final Map<String, Number> clicksAtDate = new HashMap<>();

        for (ClickRecord click : clicks) {
            final String roundedDate = roundDate(click.getDate(), granularity);
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

        for (ImpressionRecord impression : impressions) {
            final String roundedDate = roundDate(impression.getDate(), granularity);
            if (!impressionSeenDates.contains(roundedDate)) {
                distinctImpressionCosts.putIfAbsent(roundedDate, impression.getCost());
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + impression.getCost()); // Not sure this line ever runs
                impressionSeenDates.add(roundedDate);
            } else {
                distinctImpressionCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + impression.getCost());
            }
        }

        for (Map.Entry<String, Number> click : clicksAtDate.entrySet()) {
            double cost = 0;

            cost += distinctClicksCosts.get(click.getKey()).doubleValue();
            cost += distinctImpressionCosts.get(click.getKey()).doubleValue();

            cpcs.put(click.getKey(), cost / click.getValue().doubleValue());
        }

        return mapToSeries("Cost-per-click" + addedInfo, cpcs);
    }

    public static double getCPC(List<ImpressionRecord> impressions, List<ClickRecord> clicks) {
        return getTotalCost(impressions, clicks) / clicks.size();
    }

    // TODO
    public static XYChart.Series<String, Number> getBounceRateTimeSeries(String addedInfo, TimeGranularity granularity, List<ClickRecord> clicks, List<InteractionRecord> interactions) {
        final Map<String, Integer> totalClicks = new HashMap<>();
        final Map<String, Integer> totalInteractions = new HashMap<>();

        for (ClickRecord click : clicks) {
            final String roundedTime = roundDate(click.getDate(), granularity);
            totalClicks.putIfAbsent(roundedTime, 0);
            totalClicks.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        for (InteractionRecord interaction : interactions) {
            final String roundedTime = roundDate(interaction.getEntryDate(), granularity);
            totalInteractions.putIfAbsent(roundedTime, 0);
            if (interaction.getConversion()) {
                totalInteractions.computeIfPresent(roundedTime, (key, val) -> val + 1);
            }
        }

        final Map<String, Number> bounceRates = new HashMap<>();
        for (Map.Entry<String, Integer> entry : totalClicks.entrySet()) {
            bounceRates.put(entry.getKey(), (double) totalInteractions.get(entry.getKey()) / entry.getValue());
        }

        return mapToSeries("Bounce rate" + addedInfo, bounceRates);
    }

    public static double getBounceRate(List<ClickRecord> clicks, List<InteractionRecord> interactions) {
        return (double) getBounces(interactions) / clicks.size();
    }

    //    private static <U> XYChart.Series<String, U> mapToSeries(String seriesName, Map<Date, U> map) {
//        final XYChart.Series<String, U> series = new XYChart.Series<>();
//        series.setName(seriesName);
//
//        final List<Map.Entry<Date, U>> orderedEntries = map.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByKey())
//                .collect(Collectors.toList());
//
//        for (Map.Entry<Date, U> entry : orderedEntries) {
//            series.getData().add(new XYChart.Data<>(dateToString(entry.getKey()), entry.getValue()));
//        }
//
//        return series;
//    }
    private static <U> XYChart.Series<String, U> mapToSeries(String seriesName, Map<String, U> map) {
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