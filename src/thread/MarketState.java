package thread;

public class MarketState {
    private static boolean marketOpen = false;

    public static synchronized boolean isOpen() {
        return marketOpen;
    }

    public static synchronized void openMarket() {
        marketOpen = true;
        System.out.println("[MARKET] Market trading session OPENED.");
    }

    public static synchronized void closeMarket() {
        marketOpen = false;
        System.out.println("[MARKET] Market trading session CLOSED.");
    }

    private MarketState() {
    }
}
