package util;

import java.util.HashSet;

public class InputValidator {
    private static final HashSet<String> VALID_SYMBOLS = new HashSet<String>();

    static {
        VALID_SYMBOLS.add("APEX");
        VALID_SYMBOLS.add("NOVA");
        VALID_SYMBOLS.add("ZENT");
        VALID_SYMBOLS.add("BOLT");
        VALID_SYMBOLS.add("SKYP");
        VALID_SYMBOLS.add("CRST");
        VALID_SYMBOLS.add("NXGN");
        VALID_SYMBOLS.add("GRNA");
    }

    public static boolean isNonEmptyString(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidUsername(String username) {
        if (username == null)
            return false;
        String trimmed = username.trim();
        return trimmed.length() >= 3 && trimmed.length() <= 50;
    }

    public static boolean isValidPassword(String password) {
        if (password == null)
            return false;
        return password.trim().length() >= 3;
    }

    public static boolean isPositiveInteger(String input) {
        if (input == null || input.trim().isEmpty())
            return false;
        try {
            int val = Integer.parseInt(input.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String input) {
        if (input == null || input.trim().isEmpty())
            return false;
        try {
            double val = Double.parseDouble(input.trim());
            return val > 0.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidStockSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty())
            return false;
        return VALID_SYMBOLS.contains(symbol.trim().toUpperCase());
    }

    public static void registerSymbol(String symbol) {
        if (symbol != null && !symbol.trim().isEmpty()) {
            VALID_SYMBOLS.add(symbol.trim().toUpperCase());
        }
    }
}
