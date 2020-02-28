package group18.dashboard.model;

public class Campaign {

    private final String IMPRESSION_LOG_PATH;
    private final String SERVER_LOG_PATH;
    private final String CLICK_LOG_PATH;

    public Campaign(String impression_path, String server_log_path, String click_log_path) {
        IMPRESSION_LOG_PATH = impression_path;
        SERVER_LOG_PATH = server_log_path;
        CLICK_LOG_PATH = click_log_path;
    }

    String name;

    //ObservableList<String>
}
