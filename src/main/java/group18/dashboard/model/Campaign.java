package group18.dashboard.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Campaign {

    private ObservableList<Impression> impressions = FXCollections.observableArrayList();
    private ObservableList<Click> clicks = FXCollections.observableArrayList();
    private ObservableList<Interaction> interactions = FXCollections.observableArrayList();

    public void readInteractions(String filepath) throws Exception {
        //Servers
        File serversFile = new File(filepath + File.separator + "server_log.csv");
        BufferedReader brServers = new BufferedReader(new FileReader(serversFile));
        String line = "";
        //First line is the column headings so should be ignored
        line = brServers.readLine();
        while ((line = brServers.readLine()) != null) {
            interactions.add(new Interaction((line)));
        }
    }

    public void readClicks(String filepath) throws Exception {
        //Clicks
        File clicksFile = new File(filepath + File.separator + "click_log.csv");
        BufferedReader brClicks = new BufferedReader(new FileReader(clicksFile));
        String line = "";
        //First line is the column headings so should be ignored
        line = brClicks.readLine();
        while ((line = brClicks.readLine()) != null) {
            clicks.add(new Click((line)));
        }
    }

    public void readImpressions(String filepath) throws Exception {
        //Impressions
        File impressionsFile = new File(filepath + File.separator + "impression_log.csv");
        BufferedReader brImpressions = new BufferedReader(new FileReader(impressionsFile));
        String line = "";
        //First line is the column headings so should be ignored
        line = brImpressions.readLine();
        while ((line = brImpressions.readLine()) != null) {
            impressions.add(new Impression((line)));
        }
    }

    public ObservableList<Impression> getImpressions() {
        return impressions;
    }

    public ObservableList<Click> getClicks() {
        return clicks;
    }

    public ObservableList<Interaction> getInteractions() {
        return interactions;
    }
}
