package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Constants {

    public static final String DB_URL;
    public static final String DB_USER;
    public static final String DB_PASSWORD;
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        Properties props = new Properties();
        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propFile)) {
                props.load(fis);
            } catch (IOException e) {

            }
        }

        String url = props.getProperty("db.url");
        if (url == null || url.trim().isEmpty()) {
            url = System.getenv("DB_URL");
        }
        DB_URL = (url != null && !url.trim().isEmpty()) ? url.trim() : "jdbc:mysql://localhost:3306/apex_trade";

        String user = props.getProperty("db.user");
        if (user == null || user.trim().isEmpty()) {
            user = System.getenv("DB_USER");
        }
        DB_USER = (user != null && !user.trim().isEmpty()) ? user.trim() : "root";

        String pass = props.getProperty("db.password");
        if (pass == null || pass.trim().isEmpty()) {
            pass = System.getenv("DB_PASSWORD");
        }
        DB_PASSWORD = (pass != null && !pass.trim().isEmpty()) ? pass.trim() : "root";
    }

    public static final double DEFAULT_BALANCE = 100000.00;
    public static final int ORDER_QUEUE_CAPACITY = 50;
    public static final int PRICE_UPDATE_INTERVAL_MS = 5000;
    public static final int ORDER_BOOK_DISPLAY_COUNT = 5;

    public static final String ORDER_TYPE_BUY = "BUY";
    public static final String ORDER_TYPE_SELL = "SELL";
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_PARTIAL = "PARTIAL";
    public static final String STATUS_FILLED = "FILLED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public static final String TRADE_LOG_FILE = "data/trade_log.txt";
    public static final String TRADE_SER_FILE = "data/trades.ser";

    public static final Object TRADE_LOCK = new Object();

    private Constants() {
    }
}
