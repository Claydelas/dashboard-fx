package group18.dashboard.model;

import group18.dashboard.exceptions.ParsingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

public class Click {

    static Function<String, Click> mapToItem = (line) -> {
        String[] columns = line.split(",");
        Click item = new Click();
        try {
            item.date = LocalDateTime.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
            item.ID = Long.parseLong(columns[1]);
            item.cost = Double.parseDouble(columns[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    };

    private LocalDateTime date;
    private long ID;
    private double cost;

    public Click() {

    }

    public Click(String lineIn) throws ParsingException {
        //Takes in a single line of the csv and parses it
        //Checks for validity as the file is parsed, throwing an exception
        String[] columns = lineIn.split(",");
        if (columns.length != 3) {
            throw new ParsingException("Incorrect number of columns were given for a click");
        }

        //Getting the date
        try {
            date = LocalDateTime.parse(columns[0], DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        } catch (DateTimeException e) {
            System.out.println(columns[0]);
            throw new ParsingException("Date could not be parsed for an impression.");
        }

        //Getting the ID
        try {
            ID = Long.parseLong(columns[1]);
        } catch (Exception e) {
            throw new ParsingException("ID could not be parsed for a click.");
        }

        //Getting the cost
        try {
            cost = Double.parseDouble(columns[2]);
        } catch (Exception e) {
            throw new ParsingException("Cost could not be parsed for a click.");
        }
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Click click = (Click) o;
        return getID() == click.getID() &&
                Objects.equals(getDate(), click.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getID());
    }

    public long getID() {
        return ID;
    }

    public double getCost() {
        return cost;
    }
}
