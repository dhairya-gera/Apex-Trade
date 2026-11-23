package thread;

public class ThreadManager {
    private Thread matchingThread;
    private PriceFluctuationThread priceThread;
    private MatchingEngine matchingEngine;

    public ThreadManager(MatchingEngine matchingEngine, PriceFluctuationThread priceThread) {
        this.matchingEngine = matchingEngine;
        this.priceThread = priceThread;

        this.matchingThread = new Thread(matchingEngine);
        this.matchingThread.setName("MatchingEngine-Thread");
        this.matchingThread.setPriority(Thread.NORM_PRIORITY + 2);
    }

    public void startAll() {
        MarketState.openMarket();
        priceThread.setName("PriceFluctuation-Thread");
        priceThread.setPriority(Thread.MIN_PRIORITY + 1);
        priceThread.setDaemon(true);
        matchingThread.start();
        priceThread.start();
        System.out.println("[THREAD-MGR] Engine & Simulation threads initialized.");
    }

    public void shutdownAll() {
        System.out.println("[THREAD-MGR] Initiating graceful shutdown of background threads...");
        MarketState.closeMarket();

        matchingThread.interrupt();
        priceThread.interrupt();

        try {
            matchingThread.join(4000);
        } catch (InterruptedException e) {
            System.err.println("[THREAD-MGR] Interrupted while joining matching thread.");
        }

        try {
            priceThread.join(2000);
        } catch (InterruptedException e) {
            System.err.println("[THREAD-MGR] Interrupted while joining price thread.");
        }
        System.out.println("[THREAD-MGR] All background threads successfully stopped.");
    }

    public MatchingEngine getMatchingEngine() {
        return this.matchingEngine;
    }
}
