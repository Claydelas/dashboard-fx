package group18.dashboard.model;

import group18.dashboard.exceptions.ParsingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

public class Interaction {

    //returns a parsed interaction object
    static Function<String, Interaction> mapToItem = (line) -> {
        String[] columns = line.split(",");
        Interaction item = new Interaction();
        try {
            item.entryDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(columns[0]);
            item.ID = Long.parseLong(columns[1]);
            if (columns[2].equals("n/a")) {
                item.exitDate = null;
            } else {
                item.exitDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(columns[2]);
            }

            item.pageViews = Integer.parseInt(columns[3]);

            if (columns[4].equals("Yes")) {
                item.conversion = true;
            } else if (columns[4].equals("No")) {
                item.conversion = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    };
    public Date entryDate;
    public Date exitDate;
    public long ID;
    public int pageViews;
    public boolean conversion;

    public Interaction() {

    }

    public Interaction(String lineIn) throws ParsingException {
        //Takes in a single line of the csv and parses it
        //Checks for validity as the file is parsed, throwing an exception
        String[] columns = lineIn.split(",");
        if (columns.length != 5) {
            throw new ParsingException("Incorrect number of columns were given for an interaction");
        }

        //Getting the entry date
        try {
            entryDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(columns[0]);
        } catch (ParseException e) {
            throw new ParsingException("Entry date could not be parsed for an interaction.");
        }

        //Getting the ID
        try {
            ID = Long.parseLong(columns[1]);
        } catch (Exception e) {
            throw new ParsingException("ID could not be parsed for an interaction.");
        }

        //Getting the exit date
        try {
            if (columns[2].equals("n/a")) {
                exitDate = null;
            } else {
                exitDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(columns[2]);
            }
        } catch (ParseException e) {
            throw new ParsingException("Exit date could not be parsed for an interaction.");
        }

        //Getting the pages viewed
        try {
            pageViews = Integer.parseInt(columns[3]);
        } catch (Exception e) {
            throw new ParsingException("Page views could not be parsed for a server.");
        }

        //Getting the conversion
        try {
            if (columns[4].equals("Yes")) {
                conversion = true;
            } else if (columns[4].equals("No")) {
                conversion = false;
            } else {
                throw new ParsingException("Conversion status could not be parsed for an interaction");
            }
        } catch (Exception e) {
            throw new ParsingException("Conversion status could not be parsed for an interaction");
        }
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public Date getExitDate() {
        return exitDate;
    }

    public long getID() {
        return ID;
    }

    public int getPageViews() {
        return pageViews;
    }

    public boolean isConversion() {
        return conversion;
    }
}
