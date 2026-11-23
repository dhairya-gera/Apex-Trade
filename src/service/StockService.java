package service;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import database.StockDAO;
import model.Stock;
import exception.StockNotFoundException;

public class StockService {
    private StockDAO stockDAO;

    public StockService(StockDAO stockDAO) {
        this.stockDAO = stockDAO;
    }

    public ArrayList<Stock> getAllStocks() {
        return stockDAO.findAll();
    }

    public Stock getStockBySymbol(String symbol) throws StockNotFoundException {
        return stockDAO.findBySymbol(symbol);
    }

    public Stock getStockById(int stockId) throws StockNotFoundException {
        return stockDAO.findById(stockId);
    }

    public TreeMap<String, ArrayList<Stock>> getStocksBySector() {
        TreeMap<String, ArrayList<Stock>> map = new TreeMap<String, ArrayList<Stock>>();
        ArrayList<Stock> stocks = stockDAO.findAll();
        for (int i = 0; i < stocks.size(); i++) {
            Stock s = stocks.get(i);
            String sector = s.getSector();
            if (!map.containsKey(sector)) {
                map.put(sector, new ArrayList<Stock>());
            }
            map.get(sector).add(s);
        }
        return map;
    }

    public LinkedHashSet<String> getUniqueSectors() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        ArrayList<Stock> stocks = stockDAO.findAll();
        Iterator<Stock> it = stocks.iterator();
        while (it.hasNext()) {
            set.add(it.next().getSector());
        }
        return set;
    }

    public String formatStockSummary(Stock s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Stock Symbol : ").append(s.getSymbol()).append("\n")
                .append("Company Name : ").append(s.getCompanyName()).append("\n")
                .append("Current Price: INR ").append(String.format("%.2f", s.getCurrentPrice())).append("\n")
                .append("Prev Close   : INR ").append(String.format("%.2f", s.getPreviousClose())).append("\n")
                .append("Change       : ").append(String.format("%+.2f%%", s.getChangePercent())).append("\n")
                .append("Sector       : ").append(s.getSector());
        return sb.toString();
    }
}
