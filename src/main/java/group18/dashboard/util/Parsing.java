package group18.dashboard.util;

import group18.dashboard.database.enums.ImpressionAge;
import group18.dashboard.database.enums.ImpressionContext;
import group18.dashboard.database.enums.ImpressionGender;
import group18.dashboard.database.enums.ImpressionIncome;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static group18.dashboard.database.tables.Click.CLICK;
import static group18.dashboard.database.tables.Impression.IMPRESSION;
import static group18.dashboard.database.tables.Interaction.INTERACTION;

public class Parsing {

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Enum<? extends Enum<?>> toEnum(String s) {
        switch (s) {
            case "<25":
                return ImpressionAge._3c25;
            case "25-34":
                return ImpressionAge._25_34;
            case "35-44":
                return ImpressionAge._35_44;
            case "45-54":
                return ImpressionAge._45_54;
            case ">54":
                return ImpressionAge._3e54;
            case "Social Media":
                return ImpressionContext.Social_Media;
            case "News":
                return ImpressionContext.News;
            case "Shopping":
                return ImpressionContext.Shopping;
            case "Blog":
                return ImpressionContext.Blog;
            case "Hobbies":
                return ImpressionContext.Hobbies;
            case "Travel":
                return ImpressionContext.Travel;
        }
        return null;
    }

    public static Consumer<String> toClick(DSLContext query, int campaignID) {
        return (line) -> {
            String[] columns = line.split(",");

            query.insertInto(CLICK,
                    CLICK.DATE, CLICK.USER, CLICK.COST, CLICK.CID)
                    .values(
                            LocalDateTime.parse(columns[0], dateFormat),
                            Long.valueOf(columns[1]),
                            Double.valueOf(columns[2]),
                            campaignID)
                    .execute();
        };
    }

    public static Consumer<String> toInteraction(DSLContext query, int campaignID) {
        return (line) -> {
            String[] columns = line.split(",");

            query.insertInto(INTERACTION,
                    INTERACTION.ENTRY_DATE, INTERACTION.USER, INTERACTION.EXIT_DATE,
                    INTERACTION.VIEWS, INTERACTION.CONVERSION, INTERACTION.CID)
                    .values(
                            LocalDateTime.parse(columns[0], dateFormat),
                            Long.valueOf(columns[1]),
                            !columns[2].equals("n/a") ? LocalDateTime.parse(columns[2], dateFormat) : null,
                            Integer.parseInt(columns[3]),
                            columns[4].equalsIgnoreCase("Yes"),
                            campaignID)
                    .execute();
        };
    }

    public static Consumer<String> toImpression(DSLContext query, int campaignID) {
        return (line) -> {
            String[] columns = line.split(",");

            query.insertInto(IMPRESSION,
                    IMPRESSION.DATE, IMPRESSION.USER, IMPRESSION.GENDER, IMPRESSION.AGE,
                    IMPRESSION.INCOME, IMPRESSION.CONTEXT, IMPRESSION.COST, IMPRESSION.CID)
                    .values(
                            LocalDateTime.parse(columns[0], dateFormat),
                            Long.valueOf(columns[1]),
                            ImpressionGender.valueOf(columns[2]),
                            (ImpressionAge) toEnum(columns[3]),
                            ImpressionIncome.valueOf(columns[4]),
                            (ImpressionContext) toEnum(columns[5]),
                            Double.valueOf(columns[6]),
                            campaignID)
                    .execute();
        };
    }
}
