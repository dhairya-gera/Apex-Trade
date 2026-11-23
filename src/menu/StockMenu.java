package menu;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
import model.Stock;
import model.Order;
import model.OrderBook;
import service.StockService;
import thread.MatchingEngine;
import exception.StockNotFoundException;
import util.TableFormatter;
import util.Constants;

public class StockMenu extends AbstractMenu {
    private StockService stockService;
    private MatchingEngine matchingEngine;

    public StockMenu(Scanner scanner, StockService stockService, MatchingEngine matchingEngine) {
        super(scanner, "STOCK MARKET EXPLORER",
                new String[] { "List All Listed Companies", "View Stock Details & Order Book",
                        "Browse Stocks by Sector", "Back to Dashboard" });
        this.stockService = stockService;
        this.matchingEngine = matchingEngine;
    }

    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1:
                listAllStocks();
                return true;
            case 2:
                viewStockOrderBook();
                return true;
            case 3:
                viewStocksBySector();
                return true;
            case 4:
                return false;
            default:
                System.out.println("  [!] Invalid choice. Enter 1 to 4.");
                return true;
        }
    }

    private void listAllStocks() {
        ArrayList<Stock> stocks = stockService.getAllStocks();
        System.out.println("\n--- [Active Market Tickers] ---");
        TableFormatter.printTable(stocks);
    }

    private void viewStockOrderBook() {
        String symbol = readPrompt("Enter Stock Symbol (e.g., APEX, NOVA, ZENT): ");
        try {
            Stock stock = stockService.getStockBySymbol(symbol);

            String[] labels = { "Symbol", "Company", "Current Price (INR)", "Previous Close", "Daily Change", "Sector",
                    "Total Shares" };
            String[] values = {
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    String.format("%.2f", stock.getCurrentPrice()),
                    String.format("%.2f", stock.getPreviousClose()),
                    String.format("%+.2f%%", stock.getChangePercent()),
                    stock.getSector(),
                    String.format("%,d", stock.getTotalShares())
            };
            TableFormatter.printDetails("Stock Profile: " + stock.getSymbol(), labels, values);

            renderOrderBook(stock.getStockId(), stock.getSymbol());

        } catch (StockNotFoundException e) {
            System.out.println("  [!] Lookup error: " + e.getMessage());
        }
    }

    private void renderOrderBook(int stockId, String symbol) {
        OrderBook book = matchingEngine.getOrderBook(stockId);
        int topN = Constants.ORDER_BOOK_DISPLAY_COUNT;

        System.out.println("==================================================");
        System.out.println("  LIVE DEPTH OF MARKET (ORDER BOOK): " + symbol);
        System.out.println("==================================================");

        ArrayList<Order> buys = book.getTopBuyOrders(topN);
        System.out.println("\n  [BID / BUY QUEUE - Highest Price First]");
        if (buys.isEmpty()) {
            System.out.println("    (No active buy limit orders in queue)");
        } else {
            String[] headers = { "Price (INR)", "Total Qty", "Remaining Qty" };
            String[][] data = new String[buys.size()][3];
            Iterator<Order> it = buys.iterator();
            int r = 0;
            while (it.hasNext()) {
                Order o = it.next();
                data[r][0] = String.format("%.2f", o.getPrice());
                data[r][1] = String.valueOf(o.getQuantity());
                data[r][2] = String.valueOf(o.getRemainingQuantity());
                r++;
            }
            TableFormatter.printGrid(headers, data);
        }

        ArrayList<Order> sells = book.getTopSellOrders(topN);
        System.out.println("\n  [ASK / SELL QUEUE - Lowest Price First]");
        if (sells.isEmpty()) {
            System.out.println("    (No active sell limit orders in queue)");
        } else {
            String[] headers = { "Price (INR)", "Total Qty", "Remaining Qty" };
            String[][] data = new String[sells.size()][3];
            for (int i = 0; i < sells.size(); i++) {
                Order o = sells.get(i);
                data[i][0] = String.format("%.2f", o.getPrice());
                data[i][1] = String.valueOf(o.getQuantity());
                data[i][2] = String.valueOf(o.getRemainingQuantity());
            }
            TableFormatter.printGrid(headers, data);
        }
    }

    private void viewStocksBySector() {
        TreeMap<String, ArrayList<Stock>> sectorMap = stockService.getStocksBySector();
        System.out.println("\n--- [Companies Grouped by Sector] ---");
        Set<String> sectors = sectorMap.keySet();
        Iterator<String> it = sectors.iterator();
        while (it.hasNext()) {
            String sector = it.next();
            System.out.println("\n  Sector: [" + sector + "]");
            ArrayList<Stock> list = sectorMap.get(sector);
            for (int i = 0; i < list.size(); i++) {
                Stock s = list.get(i);
                System.out.println(String.format("    %-6s | %-25s | INR %8.2f (%+.2f%%)",
                        s.getSymbol(), s.getCompanyName(), s.getCurrentPrice(), s.getChangePercent()));
            }
        }
    }
}
