package group18.dashboard.model;

import group18.dashboard.exceptions.ParsingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

public class Impression {

    //returns a parsed impression object
    static Function<String, Impression> mapToItem = (line) -> {
        String[] p = line.split(",");
        Impression item = new Impression();
        try {
            item.date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(p[0]);
            item.ID = Long.parseLong(p[1]);
            item.gender = p[2];

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
            item.income = p[4];
            item.context = p[5];
            item.cost = Double.parseDouble(p[6]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return item;
    };
    public Date date;
    public long ID;
    public String gender;
    public int lowerBound = -1;
    public int upperBound = -1;
    public String income;
    public String context;
    public double cost;

    public Impression(String lineIn) throws ParsingException {
        //Takes in a single line of the csv and parses it
        //Checks for validity as the file is parsed, throwing an exception
        String[] columns = lineIn.split(",");
//        if (columns.length!=7){
//            throw new ParsingException("Incorrect number of columns were given for an impression");
//        }

        //Getting the date
        try {
            date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(columns[0]);

        } catch (ParseException e) {
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
            gender = columns[2];
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
            income = columns[4];
        } catch (Exception e) {
            throw new ParsingException("Income could not be parsed for an impression.");
        }

        try {
            context = columns[5];
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

    public String getGender() {
        return gender;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public String getIncome() {
        return income;
    }

    public String getContext() {
        return context;
    }

    public double getCost() {
        return cost;
    }
}

