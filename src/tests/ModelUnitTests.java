
import static org.junit.jupiter.api.Assertions.*;

import group18.dashboard.exceptions.ParsingException;
import group18.dashboard.model.Click;
import group18.dashboard.model.Impression;
import group18.dashboard.model.Interaction;

import org.junit.jupiter.api.Test;

import java.util.Date;


public class ModelUnitTests{

    @Test
    public void parseInteractionTestCorrect() {
        //Testing correct interaction parsing
        Interaction i = null;
        try {
            i = new Interaction("2015-01-01 12:01:21,8895519749317550080,2015-01-01 12:05:13,7,No");
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        System.out.println(i.getEntryDate());
        //Date class constructor requires (year-1900, month indexed from 0, date indexed from 1,
        //  hours indexed from 0, mins indexed from 1, seconds indexed from 1)
        //This is not mentioned in the javadoc
        Date entryDate = new Date(2015 - 1900, 0, 1, 00, 1, 21);
        assertEquals(entryDate, i.getEntryDate());

        long id = 8895519749317550080L;
        assertEquals(id, i.getID());

        Date exitDate = new Date(2015 - 1900, 0, 1, 00, 5, 13);
        assertEquals(exitDate, i.getExitDate());

        int pageViews = 7;
        assertEquals(pageViews, i.getPageViews());

        boolean isConversion = false;
        assertEquals(isConversion, i.isConversion());
    }
    @Test
    public void parseInteractionTestIncorrect(){
        //Testing incorrect data input
        assertThrows( ParsingException.class, () -> new Interaction("2015-01-01 12:01:21,8895519749317550080,2015-01-01 12:05:13,No"));
        assertThrows( ParsingException.class, () -> new Interaction("2015-01-01 12:01:21,8895519749317550080,2015-01-01 12:05:13,7,Nothing"));
        assertThrows( ParsingException.class, () -> new Interaction("2015-01-01 12:01:21,8895519749317550080,2015-01-01 12:05:13,7,No, N/a"));
        assertThrows( ParsingException.class, () -> new Interaction(",,,,,"));
        assertThrows( ParsingException.class, () -> new Interaction(",,,,"));
        assertThrows( ParsingException.class, () -> new Interaction(""));
        assertThrows( ParsingException.class, () -> new Interaction("this, is, a, test, a"));
    }

    @Test
    public void parseClickTestCorrect(){
        //Testing correct data input
        Click c = null;
        try {
            c = new Click("2015-01-01 12:01:21,8895519749317550080,11.794442");
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        Date d = new Date(2015-1900, 0, 1, 00, 01, 21);
        assertEquals(d, c.getDate());

        long id = 8895519749317550080L;
        assertEquals(id, c.getID());

        double cost = 11.794442;
        assertEquals(cost, c.getCost());
    }

    @Test
    public void parseClickTestIncorrect() {
        //Testing incorrect data input
        assertThrows(ParsingException.class, () -> new Click("2015-01-01 12:01:21,8895519749317550080,11.794442, 7"));
        assertThrows(ParsingException.class, () -> new Click("2015-01-01 12:01:21,8895519749317550080"));
        assertThrows(ParsingException.class, () -> new Click(""));
        assertThrows(ParsingException.class, () -> new Click(",,"));
        assertThrows(ParsingException.class, () -> new Click(",51,12.6"));
        assertThrows(ParsingException.class, () -> new Click("2015-01-01 14:17:03,12.6,"));
    }

    @Test
    public void parseImpressionTestCorrect(){
        Impression i = null;
        try {
            i = new Impression("2015-01-01 12:00:02,4620864431353617408,Male,25-34,High,Blog,0.001713");
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        Date date = new Date(2015-1900, 00, 01, 00, 00, 02);
        assertEquals(date, i.getDate());

        long id = 4620864431353617408L;
        assertEquals(id, i.getID());

        String gender = "Male";
        assertEquals(gender, i.getGender());

        int lowerBound = 25;
        int upperBound = 34;
        assertEquals(lowerBound, i.getLowerBound());
        assertEquals(upperBound, i.getUpperBound());

        String income = "High";
        assertEquals(income, i.getIncome());

        String context = "Blog";
        assertEquals(context, i.getContext());

        double cost = 0.001713;
        assertEquals(cost, i.getCost());
    }

    @Test
    public void parseImpressionTestIncorrect() {
        //Testing incorrect data input
        assertThrows(ParsingException.class, () -> new Impression("2015-01-01 12:00:02,4620864431353617408,Male,25-34,High,Blog"));
        assertThrows(ParsingException.class, () -> new Impression("2015-01-01 12:00:02,4620864431353617408,Male,25-34,High,Blog,0.001713, 0"));
        assertThrows(ParsingException.class, () -> new Impression(""));
        assertThrows(ParsingException.class, () -> new Impression("2"));
        assertThrows(ParsingException.class, () -> new Impression("2,"));
        assertThrows(ParsingException.class, () -> new Impression("2,,,,,,"));
        assertThrows(ParsingException.class, () -> new Impression(",,,,5,,"));
        assertThrows(ParsingException.class, () -> new Impression(",,,,,,"));
    }

}