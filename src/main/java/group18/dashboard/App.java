package group18.dashboard;

import group18.dashboard.database.tables.Campaign;
import group18.dashboard.util.DB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.SQLException;

public class App extends Application {
    public static DSLContext query;
    private static Scene scene;

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        System.getProperties().setProperty("org.jooq.no-logo", "true");
        query = DSL.using(DB.connection(), SQLDialect.H2);

        setupDB();

        if (!hasCampaigns()) {
            scene = new Scene(loadFXML("import"));
            stage.sizeToScene();
        } else {
            scene = new Scene(loadFXML("dashboard"), 1024, 600);
            stage.setMinHeight(500);
            stage.setMinWidth(800);
        }
        stage.setTitle("Ad Auction Dashboard alpha");
        stage.setScene(scene);
        stage.show();
    }

    private boolean hasCampaigns() {
        return query.fetchExists(Campaign.CAMPAIGN);
    }

    private void setupDB() {
        try {
            DB.connection().prepareStatement(
                    "create table if not exists CAMPAIGN (\n" +
                            "    CID         INT auto_increment,\n" +
                            "    NAME        VARCHAR,\n" +
                            "    IMPRESSIONS INT,\n" +
                            "    CLICKS      INT,\n" +
                            "    UNIQUES     INT,\n" +
                            "    BOUNCES     INT,\n" +
                            "    CONVERSIONS INT,\n" +
                            "    CTR         DOUBLE,\n" +
                            "    CPA         DOUBLE,\n" +
                            "    CPC         DOUBLE,\n" +
                            "    CPM         DOUBLE,\n" +
                            "    BOUNCE_RATE DOUBLE,\n" +
                            "    TOTAL_COST  DOUBLE,\n" +
                            "    PARSED      BOOLEAN default FALSE not null,\n" +
                            "    constraint CAMPAIGN_PK\n" +
                            "        primary key (CID));").execute();
            DB.connection().prepareStatement(
                    "create table if not exists IMPRESSION(\n" +
                            "    DATE         DATETIME                                                               not null,\n" +
                            "    USER         LONG                                                                   not null,\n" +
                            "    GENDER       ENUM ('Male', 'Female')                                                not null,\n" +
                            "    AGE          ENUM ('<25', '25-34', '35-44', '45-54', '>54')                         not null,\n" +
                            "    INCOME       ENUM ('Low', 'Medium', 'High')                                         not null,\n" +
                            "    CONTEXT      ENUM ('News', 'Shopping', 'Social Media', 'Blog', 'Hobbies', 'Travel') not null,\n" +
                            "    COST         DOUBLE                                                                 not null,\n" +
                            "    CID          INT                                                                    not null,\n" +
                            "    IMPRESSIONID INT auto_increment,\n" +
                            "    constraint IMPRESSION_PK\n" +
                            "        primary key (IMPRESSIONID),\n" +
                            "    constraint IMPRESSION_CAMPAIGN_CID_FK\n" +
                            "        foreign key (CID) references CAMPAIGN (CID)\n" +
                            "            on update cascade on delete cascade);").execute();
            DB.connection().prepareStatement(
                    "create table if not exists CLICK(\n" +
                            "    DATE    DATETIME not null,\n" +
                            "    USER    LONG     not null,\n" +
                            "    COST    DOUBLE   not null,\n" +
                            "    CID     INT      not null,\n" +
                            "    CLICKID INT auto_increment,\n" +
                            "    constraint CLICK_PK\n" +
                            "        primary key (CLICKID),\n" +
                            "    constraint CLICK_CAMPAIGN_CID_FK\n" +
                            "        foreign key (CID) references CAMPAIGN (CID)\n" +
                            "            on update cascade on delete cascade);").execute();
            DB.connection().prepareStatement(
                    "create table if not exists INTERACTION(\n" +
                            "    ENTRY_DATE    DATETIME not null,\n" +
                            "    USER          LONG     not null,\n" +
                            "    EXIT_DATE     DATETIME,\n" +
                            "    VIEWS         INT      not null,\n" +
                            "    CONVERSION    BOOLEAN  not null,\n" +
                            "    CID           INT      not null,\n" +
                            "    INTERACTIONID INT auto_increment,\n" +
                            "    constraint INTERACTION_PK\n" +
                            "        primary key (INTERACTIONID),\n" +
                            "    constraint INTERACTION_CAMPAIGN_CID_FK\n" +
                            "        foreign key (CID) references CAMPAIGN (CID)\n" +
                            "            on update cascade on delete cascade);").execute();
            DB.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}