package group18.dashboard;

import group18.dashboard.model.Click;
import group18.dashboard.model.Impression;
import group18.dashboard.model.Interaction;
import javafx.scene.chart.XYChart;


import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewDataParser {
    private static final DateTimeFormatter hourlyFormatter = DateTimeFormatter.ofPattern("HH:00, dd/MM/yy");
    private static final DateTimeFormatter dailyFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yy");
    private static final DateTimeFormatter weeklyFormatter = DateTimeFormatter.ofPattern("'Week' w, LLLL yyyy");
    
//    public static XYChart.Series<String, Number> getClickCostFilteredHistogram(List<Click> clicks) {
//        //final HashMap<Click, Double> clickCosts
//
//        final List<Impression> allImpressions = new ArrayList<>(/*impressions*/);
//        final List<Click> allClicks = new ArrayList<>(clicks);
//
//        final List<Impression> genderMaleImpressions = new ArrayList<>();
//        final List<Click> genderMaleClicks = new ArrayList<>();
//        final List<Impression> genderFemaleImpressions = new ArrayList<>();
//        final List<Click> genderFemaleClicks = new ArrayList<>();
//
//        final List<Impression> ageUnder25Impressions = new ArrayList<>();
//        final List<Click> ageUnder25Clicks = new ArrayList<>();
//        final List<Impression> age25to34Impressions = new ArrayList<>();
//        final List<Click> age25to34Clicks = new ArrayList<>();
//        final List<Impression> age35to44Impressions = new ArrayList<>();
//        final List<Click> age35to44Clicks = new ArrayList<>();
//        final List<Impression> age45to54Impressions = new ArrayList<>();
//        final List<Click> age45to54Clicks = new ArrayList<>();
//        final List<Impression> ageOver54Impressions = new ArrayList<>();
//        final List<Click> ageOver54Clicks = new ArrayList<>();
//
//        final List<Impression> incomeLowImpressions = new ArrayList<>();
//        final List<Click> incomeLowClicks = new ArrayList<>();
//        final List<Impression> incomeMediumImpressions = new ArrayList<>();
//        final List<Click> incomeMediumClicks = new ArrayList<>();
//        final List<Impression> incomeHighImpressions = new ArrayList<>();
//        final List<Click> incomeHighClicks = new ArrayList<>();
//
//        final List<Impression> contextNewsImpressions = new ArrayList<>();
//        final List<Click> contextNewsClicks = new ArrayList<>();
//        final List<Impression> contextShoppingImpressions = new ArrayList<>();
//        final List<Click> contextShoppingClicks = new ArrayList<>();
//        final List<Impression> contextSocialMediaImpressions = new ArrayList<>();
//        final List<Click> contextSocialMediaClicks = new ArrayList<>();
//        final List<Impression> contextBlogImpressions = new ArrayList<>();
//        final List<Click> contextBlogClicks = new ArrayList<>();
//        final List<Impression> contextHobbiesImpressions = new ArrayList<>();
//        final List<Click> contextHobbiesClicks = new ArrayList<>();
//        final List<Impression> contextTravelImpressions = new ArrayList<>();
//        final List<Click> contextTravelClicks = new ArrayList<>();
//
//        for (Impression impression : allImpressions) {
//            switch (impression.gender) {
//                case MALE:
//                    genderMaleImpressions.add(impression);
//                    break;
//                case FEMALE:
//                    genderFemaleImpressions.add(impression);
//                    break;
//            }
//
//            if (impression.upperBound == 25) {
//                ageUnder25Impressions.add(impression);
//            } else if (impression.lowerBound == 25) {
//                age25to34Impressions.add(impression);
//            } else if (impression.lowerBound == 35) {
//                age35to44Impressions.add(impression);
//            } else if (impression.lowerBound == 45) {
//                age45to54Impressions.add(impression);
//            } else if (impression.lowerBound == 54) {
//                ageOver54Impressions.add(impression);
//            } else {
//                System.err.printf("Unrecognised impression with age lowerbound %d%n", impression.lowerBound);
//            }
//
//            switch (impression.income) {
//                case LOW:
//                    incomeLowImpressions.add(impression);
//                    break;
//                case MEDIUM:
//                    incomeMediumImpressions.add(impression);
//                    break;
//                case HIGH:
//                    incomeHighImpressions.add(impression);
//                    break;
//            }
//
//            switch (impression.context) {
//                case NEWS:
//                    contextNewsImpressions.add(impression);
//                    break;
//                case SHOPPING:
//                    contextShoppingImpressions.add(impression);
//                    break;
//                case SOCIAL_MEDIA:
//                    contextSocialMediaImpressions.add(impression);
//                    break;
//                case BLOG:
//                    contextBlogImpressions.add(impression);
//                    break;
//                case HOBBIES:
//                    contextHobbiesImpressions.add(impression);
//                    break;
//                case TRAVEL:
//                    contextTravelImpressions.add(impression);
//                    break;
//            }
//        }

//        for (Click click : allClicks) {
//            switch (click.gender) {
//                case MALE:
//                    genderMaleImpressions.add(click);
//                    break;
//                case FEMALE:
//                    genderFemaleImpressions.add(click);
//                    break;
//            }
//
//            if (click.upperBound == 25) {
//                ageUnder25Impressions.add(click);
//            } else if (click.lowerBound == 25) {
//                age25to34Impressions.add(click);
//            } else if (click.lowerBound == 35) {
//                age35to44Impressions.add(click);
//            } else if (click.lowerBound == 45) {
//                age45to54Impressions.add(click);
//            } else if (click.lowerBound == 54) {
//                ageOver54Impressions.add(click);
//            } else {
//                System.err.printf("Unrecognised click with age lowerbound %d%n", click.lowerBound);
//            }
//
//            switch (click.income) {
//                case LOW:
//                    incomeLowImpressions.add(click);
//                    break;
//                case MEDIUM:
//                    incomeMediumImpressions.add(click);
//                    break;
//                case HIGH:
//                    incomeHighImpressions.add(click);
//                    break;
//            }
//
//            switch (click.context) {
//                case NEWS:
//                    contextNewsImpressions.add(click);
//                    break;
//                case SHOPPING:
//                    contextShoppingImpressions.add(click);
//                    break;
//                case SOCIAL_MEDIA:
//                    contextSocialMediaImpressions.add(click);
//                    break;
//                case BLOG:
//                    contextBlogImpressions.add(click);
//                    break;
//                case HOBBIES:
//                    contextHobbiesImpressions.add(click);
//                    break;
//                case TRAVEL:
//                    contextTravelImpressions.add(click);
//                    break;
//            }
//        }
//
//        return null;
//    }

    // timeResolution parameter filled by Calendar.MONTH, Calendar.MINUTE, etc.
    // Fills all 'Number of X' metrics charts
//    public static XYChart.Series<String, Number> getCumulativeTimeSeries(String dataName, TimeGranularity granularity, List<Date> times) {
//        final Map<Date, Number> quantities = new HashMap<>();
//
//        for (Date time : times) {
//            final Date roundedTime = DateUtils.round(time, timeResolution);
//            quantities.putIfAbsent(roundedTime, 0);
//            quantities.computeIfPresent(roundedTime, (key, value) -> value.intValue() + 1);
//        }
//
//        return mapToSeries(dataName, quantities);
//    }


    private static XYChart.Series<String, Double> getClickCostsHistogram(List<Click> clicks, Function<Click, Integer> getClickTime, int timeDivisions, Function<Integer, String> showDivision) {
        int[] clicksPerTime = new int[timeDivisions];
        double[] costsPerTime = new double[timeDivisions];

        for (Click click : clicks) {
            final int time = getClickTime.apply(click);

            clicksPerTime[time] += 1;
            costsPerTime[time] += click.getCost();
        }

        XYChart.Series<String, Double> clickCosts = new XYChart.Series<>();
        for (int i = 0; i < timeDivisions; i++) {
            clickCosts.getData().add(new XYChart.Data<>(
                    showDivision.apply(i), costsPerTime[i] / clicksPerTime[i]
            ));
        }

        return clickCosts;
    }

    public static XYChart.Series<String, Double> getDailyClickCostsHistogram(List<Click> clicks) {
        return getClickCostsHistogram(
                clicks,
                c -> c.getDate().getDayOfWeek().getValue(),
                7,
                d -> DayOfWeek.of(d).toString()
        );
    }

    public static XYChart.Series<String, Double> getHourlyClickCostsHistogram(List<Click> clicks) {
        return getClickCostsHistogram(
                clicks,
                c -> c.getDate().getHour(),
                24,
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
    public static XYChart.Series<String, Number> getCPMTimeSeries(TimeGranularity granularity, List<Impression> impressions, List<Click> clicks) {
        final Map<String, Number> cpms = new HashMap<>();
        final Map<String, Number> cpmDatesNumber = new HashMap<>();
        final Map<String, Number> cpmDatesCost = new HashMap<>();

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
                            click.getDate().isAfter(firstImpression.getDate()) &&
                                    click.getDate().isBefore(lastImpression.getDate()))
                    .mapToDouble(Click::getCost)
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

        return mapToSeries("Cost-per-mille", cpms);
    }

    public static double getCPM(List<Impression> impressions, List<Click> clicks) {
        return getTotalCost(impressions, clicks) / (impressions.size() * 1000);
    }

    public static XYChart.Series<String, Number> getTotalCostSeries(TimeGranularity granularity, List<Impression> impressions, List<Click> clicks) {
        final Map<String, Number> totalCosts = new HashMap<>();

        for (Impression impression : impressions) {
            final String roundedTime = roundDate(impression.getDate(), granularity);
            totalCosts.putIfAbsent(roundedTime, 0.0);
            totalCosts.computeIfPresent(roundedTime, (key, val) -> val.doubleValue() + impression.getCost());
        }

        for (Click click : clicks) {
            final String roundedTime = roundDate(click.getDate(), granularity);
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

    public static XYChart.Series<String, Number> getCTRTimeSeries(TimeGranularity granularity, List<Impression> impressions, List<Click> clicks) {
        final Map<String, Integer> totalImpressions = new HashMap<>();
        final Map<String, Integer> totalClicks = new HashMap<>();

        for (Impression impression : impressions) {
            final String roundedTime = roundDate(impression.getDate(), granularity);
            totalImpressions.putIfAbsent(roundedTime, 0);
            totalImpressions.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        for (Click click : clicks) {
            final String roundedTime = roundDate(click.getDate(), granularity);
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

    public static XYChart.Series<String, Number> getCPATimeSeries(TimeGranularity granularity, List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        final Map<String, Number> cpas = new HashMap<>();
        final List<String> clickSeenDates = new ArrayList<>();
        final List<String> impressionSeenDates = new ArrayList<>();
        final List<String> interactionSeenDates = new ArrayList<>();

        final Map<String, Number> distinctClicksCosts = new HashMap<>();
        final Map<String, Number> distinctImpressionCosts = new HashMap<>();
        final Map<String, Number> acquisitionsAtDate = new HashMap<>();

        for (Click click : clicks) {
            final String roundedDate = roundDate(click.getDate(), granularity);
            if (!clickSeenDates.contains(roundedDate)) {
                distinctClicksCosts.putIfAbsent(roundedDate, click.getCost());
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + click.getCost()); // Not sure this line ever runs
                clickSeenDates.add(roundedDate);
            } else {
                distinctClicksCosts.computeIfPresent(roundedDate, (key, d) -> d.doubleValue() + click.getCost());
            }
        }

        for (Impression impression : impressions) {
            final String roundedDate = roundDate(impression.getDate(), granularity);
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

        return mapToSeries("Cost-per-acquisition", cpas);
    }

    public static double getCPA(List<Impression> impressions, List<Click> clicks, List<Interaction> interactions) {
        return getTotalCost(impressions, clicks) / getConversions(interactions);
    }

    public static XYChart.Series<String, Number> getCPCTimeSeries(TimeGranularity granularity, List<Impression> impressions, List<Click> clicks) {
        final Map<String, Number> cpcs = new HashMap<>();
        final List<String> clickSeenDates = new ArrayList<>();
        final List<String> impressionSeenDates = new ArrayList<>();

        final Map<String, Number> distinctClicksCosts = new HashMap<>();
        final Map<String, Number> distinctImpressionCosts = new HashMap<>();
        final Map<String, Number> clicksAtDate = new HashMap<>();

        for (Click click : clicks) {
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

        for (Impression impression : impressions) {
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

        return mapToSeries("Cost-per-click", cpcs);
    }

    public static double getCPC(List<Impression> impressions, List<Click> clicks) {
        return getTotalCost(impressions, clicks) / clicks.size();
    }

    // TODO
    public static XYChart.Series<String, Number> getBounceRateTimeSeries(TimeGranularity granularity, List<Click> clicks, List<Interaction> interactions) {
        final Map<String, Integer> totalClicks = new HashMap<>();
        final Map<String, Integer> totalInteractions = new HashMap<>();

        for (Click click : clicks) {
            final String roundedTime = roundDate(click.getDate(), granularity);
            totalClicks.putIfAbsent(roundedTime, 0);
            totalClicks.computeIfPresent(roundedTime, (key, val) -> val + 1);
        }

        for (Interaction interaction : interactions) {
            final String roundedTime = roundDate(interaction.getEntryDate(), granularity);
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