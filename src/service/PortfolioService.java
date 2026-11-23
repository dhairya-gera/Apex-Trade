package service;

import java.util.ArrayList;
import java.util.Iterator;
import database.PortfolioDAO;
import database.StockDAO;
import model.Portfolio;

public class PortfolioService {
    private PortfolioDAO portfolioDAO;
    private StockDAO stockDAO;

    public PortfolioService(PortfolioDAO portfolioDAO, StockDAO stockDAO) {
        this.portfolioDAO = portfolioDAO;
        this.stockDAO = stockDAO;
    }

    public ArrayList<Portfolio> getUserPortfolio(int userId) {
        return portfolioDAO.findByUser(userId);
    }

    public double getTotalPortfolioValue(int userId) {
        ArrayList<Portfolio> holdings = portfolioDAO.findByUser(userId);
        double total = 0.0;
        Iterator<Portfolio> it = holdings.iterator();
        while (it.hasNext()) {
            Portfolio p = it.next();
            total += p.getCurrentValue();
        }
        return total;
    }

    public double getTotalInvestedValue(int userId) {
        ArrayList<Portfolio> holdings = portfolioDAO.findByUser(userId);
        double total = 0.0;
        for (int i = 0; i < holdings.size(); i++) {
            total += holdings.get(i).getInvestedValue();
        }
        return total;
    }

    public double getTotalProfitLoss(int userId) {
        return getTotalPortfolioValue(userId) - getTotalInvestedValue(userId);
    }
}
