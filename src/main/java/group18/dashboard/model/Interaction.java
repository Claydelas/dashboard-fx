package group18.dashboard.model;

import group18.dashboard.exceptions.ParsingException;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Interaction {
    private Date entryDate;
    private Date exitDate;
    private long ID;
    private int pageViews;
    private boolean conversion;

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
            if(columns[2].equals("n/a")){
                exitDate = null;
            }
            else {
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
