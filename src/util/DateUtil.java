package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_PATTERN);
        return sdf.format(new Date());
    }

    public static String formatTimestamp(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "N/A";
        }
        if (raw.length() > 19) {
            return raw.substring(0, 19);
        }
        return raw;
    }
}
