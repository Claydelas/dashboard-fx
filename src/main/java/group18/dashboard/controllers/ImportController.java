package group18.dashboard.controllers;

import group18.dashboard.database.enums.ImpressionAge;
import group18.dashboard.database.enums.ImpressionContext;
import group18.dashboard.database.enums.ImpressionGender;
import group18.dashboard.database.enums.ImpressionIncome;
import group18.dashboard.util.DB;
import group18.dashboard.util.Parsing;
import javafx.fxml.FXML;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import static group18.dashboard.database.tables.Click.CLICK;
import static group18.dashboard.database.tables.Impression.IMPRESSION;
import static group18.dashboard.database.tables.Interaction.INTERACTION;

public class ImportController {

    DSLContext query;

    @FXML
    public void initialize() {
        query = DSL.using(DB.connection(), SQLDialect.H2);
    }

    private Consumer<String> toClick = (line) -> {
        String[] columns = line.split(",");

        query.insertInto(CLICK,
                CLICK.DATE, CLICK.USER, CLICK.COST, CLICK.CID)
                .values(
                        LocalDateTime.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        Long.valueOf(columns[1]),
                        Double.valueOf(columns[2]),
                        1)
                .execute();
    };
    private Consumer<String> toInteraction = (line) -> {
        String[] columns = line.split(",");

        query.insertInto(INTERACTION,
                INTERACTION.ENTRY_DATE, INTERACTION.USER, INTERACTION.EXIT_DATE,
                INTERACTION.VIEWS, INTERACTION.CONVERSION, INTERACTION.CID)
                .values(
                        LocalDateTime.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        Long.valueOf(columns[1]),
                        !columns[2].equals("n/a") ? LocalDateTime.parse(columns[2], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null,
                        Integer.parseInt(columns[3]),
                        columns[4].equalsIgnoreCase("Yes"),
                        1)
                .execute();
    };

    private Consumer<String> toImpression = (line) -> {
        String[] columns = line.split(",");

        query.insertInto(IMPRESSION,
                IMPRESSION.DATE, IMPRESSION.USER, IMPRESSION.GENDER, IMPRESSION.AGE,
                IMPRESSION.INCOME, IMPRESSION.CONTEXT, IMPRESSION.COST, IMPRESSION.CID)
                .values(
                        LocalDateTime.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        Long.valueOf(columns[1]),
                        ImpressionGender.valueOf(columns[2]),
                        (ImpressionAge) Parsing.toEnum(columns[3]),
                        ImpressionIncome.valueOf(columns[4]),
                        (ImpressionContext) Parsing.toEnum(columns[5]),
                        Double.valueOf(columns[6]),
                        1)
                .execute();
    };

    public void parse(String path, Consumer<String> consumer) throws Exception {
        File file = new File(path);
        System.out.println("Parsing " + file.getAbsolutePath());
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        input.lines().parallel().skip(1).forEach(consumer);
        input.close();
        System.out.println("Done parsing " + file.getAbsolutePath());
    }
}
