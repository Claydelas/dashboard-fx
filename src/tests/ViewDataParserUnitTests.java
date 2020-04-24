import static group18.dashboard.ViewDataParser.*;
import static org.junit.jupiter.api.Assertions.*;

import group18.dashboard.exceptions.ParsingException;
import group18.dashboard.model.Click;
import group18.dashboard.model.Impression;
import group18.dashboard.model.Interaction;
import javafx.scene.chart.XYChart;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ViewDataParserUnitTests {

    @Test
    public void getUniquesTest(){
        List<Click> clicks = new ArrayList<>();
        try {
            clicks.add(new Click("2015-01-01 12:01:21,8895519749317550080,11.794442"));
            clicks.add(new Click("2015-01-01 12:01:21,8895519749317550080,11.794442"));
            clicks.add(new Click("2015-01-01 12:01:21,8895519749317550081,11.794442"));
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        assertEquals(2, getUniques(clicks));
    }

    @Test
    public void getBouncesTest(){
        List<Interaction> interactions = new ArrayList<>();

        try {
            interactions.add(
                    new Interaction("2015-01-01 12:01:21,8895519749317550080,2015-01-01 12:05:13,7,No"));
            interactions.add(
                    new Interaction("2015-01-01 12:01:21,8895519749317550086,2015-01-01 12:05:12,7,No"));
            interactions.add(
                    new Interaction("2015-01-01 12:01:21,8895519749317550019,2015-01-01 12:04:52,7,Yes"));
        } catch (ParsingException e){
            e.printStackTrace();
        }
        assertEquals(2, getBounces(interactions));
    }

    @Test
    public void getConversionsTest(){
        List<Interaction> interactions = new ArrayList<>();

        try {
            interactions.add(
                    new Interaction("2015-01-01 12:01:21,8895519749317550080,2015-01-01 12:05:13,7,No"));
            interactions.add(
                    new Interaction("2015-01-01 12:01:21,8895519749317550086,2015-01-01 12:05:12,7,No"));
            interactions.add(
                    new Interaction("2015-01-01 12:01:21,8895519749317550019,2015-01-01 12:04:52,7,Yes"));
        } catch (ParsingException e){
            e.printStackTrace();
        }
        assertEquals(1, getConversions(interactions));
    }

    @Test
    public void getTotalCostTest(){
        List<Impression> impressions = new ArrayList<>();
        List<Click> clicks = new ArrayList<>();
        try {
            impressions.add(
                    new Impression("2015-01-01 12:00:02,4620864431353617408,Male,25-34,High,Blog,0.001713"));
            impressions.add(
                    new Impression("2015-01-01 12:00:02,4620864431353617408,Male,25-34,High,Blog,0.001713"));
            impressions.add(
                    new Impression("2015-01-01 12:00:02,4620864431353617408,Male,25-34,High,Blog,0.001713"));
            clicks.add(new Click("2015-01-01 12:01:21,8895519749317550080,11.794442"));
            clicks.add(new Click("2015-01-01 12:01:21,8895519749317550080,11.794442"));
            clicks.add(new Click("2015-01-01 12:01:21,8895519749317550081,11.794442"));
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        double cost = (0.001713 * 3) + (11.794442 *3);
        assertEquals(cost, getTotalCost(impressions, clicks));
    }
}
