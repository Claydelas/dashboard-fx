package group18.dashboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class CSVReader {
    //Ideally one CSVReader object should be used for each campaign
    //Getters are provided for each log
    private ArrayList<Impression> impressions = new ArrayList<>();
    private ArrayList<Click> clicks = new ArrayList<>();
    private ArrayList<Server> servers = new ArrayList<>();

    public CSVReader(String filepath) throws Exception{
        // Takes in the filepath of the campaign folder and
        // generates the objects for each item

        //Impressions
        File impressionsFile = new File(filepath+File.separator+"impression_log.csv");
        BufferedReader brImpressions = new BufferedReader(new FileReader(impressionsFile));
        String line = "";
        //First line is the column headings so should be ignored
        line = brImpressions.readLine();
        while((line = brImpressions.readLine()) != null){
            impressions.add(new Impression((line)));
        }

        //Clicks
        File clicksFile = new File(filepath+File.separator+"click_log.csv");
        BufferedReader brClicks = new BufferedReader(new FileReader(clicksFile));
        line = "";
        //First line is the column headings so should be ignored
        line = brClicks.readLine();
        while((line = brClicks.readLine()) != null){
            clicks.add(new Click((line)));
        }

        //Servers
        File serversFile = new File(filepath+File.separator+"server_log.csv");
        BufferedReader brServers = new BufferedReader(new FileReader(serversFile));
        line = "";
        //First line is the column headings so should be ignored
        line = brServers.readLine();
        while((line = brServers.readLine()) != null){
            servers.add(new Server((line)));
        }
    }
    public static void main(String[] args) throws Exception{
        //Example for getting all objects from a given campaign directory
        CSVReader c = new CSVReader("C:\\Users\\Jeremy\\Documents\\year2\\Semester2\\COMP2211\\2_week_campaign_2");
    }

    public ArrayList<Impression> getImpressions() {
        return impressions;
    }

    public ArrayList<Click> getClicks() {
        return clicks;
    }

    public ArrayList<Server> getServers() {
        return servers;
    }
}
