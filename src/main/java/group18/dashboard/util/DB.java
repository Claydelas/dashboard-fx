package group18.dashboard.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DB {

    static Properties properties;
    static Connection connection;

    /**
     * Get a fresh connection from H2.
     */
    public static Connection connection() {
        if (connection == null) {
            try {
                Class.forName(driver());

                connection = DriverManager.getConnection(
                        url(),
                        username(),
                        password());
                //connection.setAutoCommit(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return connection;
    }

    public static String password() {
        return properties().getProperty("db.password");
    }

    public static String username() {
        return properties().getProperty("db.username");
    }

    public static String url() {
        return properties().getProperty("db.url");
    }

    public static String driver() {
        return properties().getProperty("db.driver");
    }

    /**
     * Get the connection properties
     */
    public static Properties properties() {
        if (properties == null) {
            try {
                properties = new Properties();
                properties.load(DB.class.getResourceAsStream("/group18/dashboard/config/config.properties"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return properties;
    }
}
