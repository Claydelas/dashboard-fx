import group18.dashboard.database.enums.ImpressionAge;
import group18.dashboard.database.enums.ImpressionContext;
import group18.dashboard.database.enums.ImpressionGender;
import group18.dashboard.database.enums.ImpressionIncome;
import group18.dashboard.database.tables.records.ClickRecord;
import group18.dashboard.database.tables.records.ImpressionRecord;
import group18.dashboard.database.tables.records.InteractionRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static group18.dashboard.ViewDataParser.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewDataParserUnitTests {
    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void getUniquesTest() {
        List<ClickRecord> clicks = new ArrayList<>();
        clicks.add(new ClickRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550080L, 11.794442, 1, 1));
        clicks.add(new ClickRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550080L, 11.794442, 1, 2));
        clicks.add(new ClickRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550081L, 11.794442, 1, 3));

        assertEquals(2, getUniques(clicks));
    }

    @Test
    public void getBouncesTest() {
        List<InteractionRecord> interactions = new ArrayList<>();

        interactions.add(
                new InteractionRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550080L, LocalDateTime.parse("2015-01-01 12:05:13", f), 7, false, 1, 1));
        interactions.add(
                new InteractionRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550086L, LocalDateTime.parse("2015-01-01 12:05:12", f), 7, false, 1, 2));
        interactions.add(
                new InteractionRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550019L, LocalDateTime.parse("2015-01-01 12:04:52", f), 7, true, 1, 3));

        assertEquals(2, getBounces(interactions));
    }

    @Test
    public void getConversionsTest() {
        List<InteractionRecord> interactions = new ArrayList<>();
        interactions.add(
                new InteractionRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550080L, LocalDateTime.parse("2015-01-01 12:05:13", f), 7, false, 1, 1));
        interactions.add(
                new InteractionRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550086L, LocalDateTime.parse("2015-01-01 12:05:12", f), 7, false, 1, 2));
        interactions.add(
                new InteractionRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550019L, LocalDateTime.parse("2015-01-01 12:04:52", f), 7, true, 1, 3));
        assertEquals(1, getConversions(interactions));
    }

    @Test
    public void getTotalCostTest() {
        List<ImpressionRecord> impressions = new ArrayList<>();
        List<ClickRecord> clicks = new ArrayList<>();
        impressions.add(
                new ImpressionRecord(LocalDateTime.parse("2015-01-01 12:00:02", f),
                        4620864431353617408L,
                        ImpressionGender.Male,
                        ImpressionAge._25_34,
                        ImpressionIncome.High,
                        ImpressionContext.Blog,
                        0.001713, 1, 1));
        impressions.add(
                new ImpressionRecord(LocalDateTime.parse("2015-01-01 12:00:02", f),
                        4620864431353617408L,
                        ImpressionGender.Male,
                        ImpressionAge._25_34,
                        ImpressionIncome.High,
                        ImpressionContext.Blog,
                        0.001713, 1, 2));
        impressions.add(
                new ImpressionRecord(LocalDateTime.parse("2015-01-01 12:00:02", f),
                        4620864431353617408L,
                        ImpressionGender.Male,
                        ImpressionAge._25_34,
                        ImpressionIncome.High,
                        ImpressionContext.Blog,
                        0.001713, 1, 3));
        clicks.add(new ClickRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550080L, 11.794442, 1, 1));
        clicks.add(new ClickRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550080L, 11.794442, 1, 2));
        clicks.add(new ClickRecord(LocalDateTime.parse("2015-01-01 12:01:21", f), 8895519749317550081L, 11.794442, 1, 3));

        double cost = (0.001713 * 3) + (11.794442 * 3);
        assertEquals(cost, getTotalCost(impressions, clicks));
    }
}
