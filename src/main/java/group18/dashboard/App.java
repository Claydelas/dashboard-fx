package group18.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

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

        scene = new Scene(loadFXML("app"), 1024, 600);
        stage.setTitle("Ad Auction Dashboard alpha");
        stage.sizeToScene();
        //stage.setMinHeight(550);
        //stage.setMinWidth(650);
        stage.setScene(scene);
        stage.show();
    }

}