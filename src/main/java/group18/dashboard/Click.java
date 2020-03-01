import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Click {
    private Date date;
    private Long ID;
    private Double cost;

    public Click(String lineIn) throws ParsingException{
        //Takes in a single line of the csv and parses it
        //Checks for validity as the file is parsed, throwing an exception
        String[] columns = lineIn.split(",");
        if (columns.length!=3){
            throw new ParsingException("Incorrect number of columns were given for a click");
        }

        //Getting the date
        try {
            date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(columns[0]);
        }
        catch (ParseException e){
            throw new ParsingException("Date could not be parsed for a click.");
        }

        //Getting the ID
        try {
            ID = Long.parseLong(columns[1]);
        }
        catch(Exception e){
            throw new ParsingException("ID could not be parsed for a click.");
        }

        //Getting the cost
        try {
            cost = Double.parseDouble(columns[2]);
        }
        catch(Exception e){
            throw new ParsingException("Cost could not be parsed for a click.");
        }
    }
    public Date getDate() {
        return date;
    }

    public Long getID() {
        return ID;
    }

    public Double getCost() {
        return cost;
    }
}
