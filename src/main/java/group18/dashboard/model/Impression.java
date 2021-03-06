package group18.dashboard.model;

import group18.dashboard.exceptions.ParsingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Function;

public class Impression {

    //returns a parsed impression object
    static Function<String, Impression> mapToItem = (line) -> {
        String[] p = line.split(",");
        Impression item = new Impression();
        try {
            item.date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(p[0]);

            item.ID = Long.parseLong(p[1]);
            item.gender = Enum.valueOf(Gender.class, p[2].toUpperCase());

            //Getting the upper and lower bounds of the age range
            if (p[3].contains("-")) {
                item.lowerBound = Integer.parseInt(p[3].split("-")[0]);
                item.upperBound = Integer.parseInt(p[3].split("-")[1]);
            } else {
                if (p[3].contains("<")) {
                    item.upperBound = Integer.parseInt(p[3].split("<")[1]);
                } else if (p[3].contains(">")) {
                    item.lowerBound = Integer.parseInt(p[3].split(">")[1]);
                }
            }
            item.income = Enum.valueOf(Income.class, p[4].toUpperCase());
            item.context = Enum.valueOf(Context.class, p[5].replace(" ", "_").toUpperCase());
            item.cost = Double.parseDouble(p[6]);
        } catch (DateTimeException | ParseException e) {
            e.printStackTrace();
        }
        return item;
    };
    public Date date;
    public long ID;
    public Gender gender;
    public int lowerBound = -1;
    public int upperBound = -1;
    public Income income;
    public Context context;
    public double cost;

    public Impression(String lineIn) throws ParsingException {
        //Takes in a single line of the csv and parses it
        //Checks for validity as the file is parsed, throwing an exception
        String[] columns = lineIn.split(",");
        if (columns.length!=7){
            throw new ParsingException("Incorrect number of columns were given for an impression");
        }

        //Getting the date
        try {
            date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(columns[0]);

        } catch (DateTimeException | ParseException e) {
            System.out.println(columns[0]);
            throw new ParsingException("Date could not be parsed for an impression.");
        }

        //Getting the ID
        try {
            ID = Long.parseLong(columns[1]);
        } catch (Exception e) {
            throw new ParsingException("ID could not be parsed for an impression.");
        }

        //Getting the gender
        try {
            gender = Enum.valueOf(Gender.class, columns[2].toUpperCase());
        } catch (Exception e) {
            throw new ParsingException("Gender could not be parsed for an impression.");
        }

        //Getting the upper and lower bounds of the age range
        if (columns[3].contains("-")) {
            lowerBound = Integer.parseInt(columns[3].split("-")[0]);
            upperBound = Integer.parseInt(columns[3].split("-")[1]);
        } else {
            if (columns[3].contains("<")) {
                upperBound = Integer.parseInt(columns[3].split("<")[1]);
            } else if (columns[3].contains(">")) {
                lowerBound = Integer.parseInt(columns[3].split(">")[1]);
            } else {
                throw new ParsingException("Age range could not be parsed for an impression.");
            }
        }

        // Getting the income
        try {
            income = Enum.valueOf(Income.class, columns[4].toUpperCase());
        } catch (Exception e) {
            throw new ParsingException("Income could not be parsed for an impression.");
        }

        try {
            context = Enum.valueOf(Context.class, columns[5].replace(" ", "_").toUpperCase());
        } catch (Exception e) {
            throw new ParsingException("Context could not be parsed for an impression.");
        }

        try {
            cost = Double.parseDouble(columns[6]);
        } catch (Exception e) {
            throw new ParsingException("Cost could not be parsed for an impression.");
        }
    }

    public Impression() {

    }

    public Date getDate() {
        return date;
    }

    public long getID() {
        return ID;
    }

    public Gender getGender() {
        return gender;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public Income getIncome() {
        return income;
    }

    public Context getContext() {
        return context;
    }

    public double getCost() {
        return cost;
    }
}

