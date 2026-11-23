package thread;

import java.util.ArrayList;
import database.StockDAO;
import model.Stock;
import util.Constants;

public class PriceFluctuationThread extends Thread {
    private StockDAO stockDAO;

    public PriceFluctuationThread(StockDAO stockDAO) {
        this.stockDAO = stockDAO;
    }

    @Override
    public void run() {
        System.out.println("[PRICE-SIM] Price fluctuation daemon active.");

        while (MarketState.isOpen()) {
            try {
                Thread.sleep(Constants.PRICE_UPDATE_INTERVAL_MS);
                if (!MarketState.isOpen()) {
                    break;
                }
                fluctuatePrices();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("[PRICE-SIM] Price fluctuation daemon stopped.");
    }

    private void fluctuatePrices() {
        ArrayList<Stock> stocks = stockDAO.findAll();
        for (int i = 0; i < stocks.size(); i++) {
            Stock s = stocks.get(i);

            double deltaPercent = (Math.random() * 3.0) - 1.5;
            if (Math.abs(deltaPercent) < 0.25) {
                deltaPercent = (deltaPercent >= 0) ? 0.35 : -0.35;
            }
            double newPrice = s.getCurrentPrice() * (1.0 + (deltaPercent / 100.0));
            if (newPrice < 1.0) {
                newPrice = 1.0;
            }
            newPrice = Math.round(newPrice * 100.0) / 100.0;
            stockDAO.updatePrice(s.getStockId(), newPrice);
        }
    }
}
