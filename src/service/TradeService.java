package service;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import database.TradeDAO;
import database.UserDAO;
import database.PortfolioDAO;
import database.OrderDAO;
import database.StockDAO;
import model.Trade;
import model.Order;
import model.User;
import exception.UserNotFoundException;
import util.Constants;
import util.DateUtil;

public class TradeService {
    private TradeDAO tradeDAO;
    private UserDAO userDAO;
    private PortfolioDAO portfolioDAO;
    private OrderDAO orderDAO;
    private StockDAO stockDAO;

    private ArrayList<Trade> sessionTrades = new ArrayList<Trade>();

    public TradeService(TradeDAO tradeDAO, UserDAO userDAO, PortfolioDAO portfolioDAO,
            OrderDAO orderDAO, StockDAO stockDAO) {
        this.tradeDAO = tradeDAO;
        this.userDAO = userDAO;
        this.portfolioDAO = portfolioDAO;
        this.orderDAO = orderDAO;
        this.stockDAO = stockDAO;
    }

    public void executeTrade(Order buyOrder, Order sellOrder, int tradeQty, double tradePrice) {
        synchronized (Constants.TRADE_LOCK) {
            try {

                User seller = userDAO.findById(sellOrder.getUserId());
                double sellerProceeds = tradePrice * tradeQty;
                userDAO.updateBalance(seller.getUserId(), seller.getBalance() + sellerProceeds);

                if (buyOrder.getPrice() > tradePrice) {
                    User buyer = userDAO.findById(buyOrder.getUserId());
                    double refundAmount = (buyOrder.getPrice() - tradePrice) * tradeQty;
                    userDAO.updateBalance(buyer.getUserId(), buyer.getBalance() + refundAmount);
                }

                portfolioDAO.upsertHolding(buyOrder.getUserId(), buyOrder.getStockId(), tradeQty, tradePrice);

                orderDAO.updateOrder(buyOrder.getOrderId(), buyOrder.getFilledQuantity(), buyOrder.getStatus());
                orderDAO.updateOrder(sellOrder.getOrderId(), sellOrder.getFilledQuantity(), sellOrder.getStatus());

                stockDAO.updatePrice(buyOrder.getStockId(), tradePrice);

                Trade trade = new Trade(
                        buyOrder.getOrderId(), sellOrder.getOrderId(),
                        buyOrder.getStockId(), buyOrder.getUserId(), sellOrder.getUserId(),
                        tradePrice, tradeQty);
                trade.setStockSymbol(buyOrder.getStockSymbol());
                trade.setExecutedAt(DateUtil.getCurrentTimestamp());
                int tradeId = tradeDAO.insertTrade(trade);
                trade.setTradeId(tradeId);

                sessionTrades.add(trade);

                logTradeToFile(trade);

                serializeTrades();

                System.out.println("\n[MATCH-ENGINE] Executed: " + trade.toLogString());

            } catch (UserNotFoundException e) {
                System.err.println("[TradeService] Execution error: " + e.getMessage());
            }
        }
    }

    private void logTradeToFile(Trade trade) {
        BufferedWriter writer = null;
        try {
            File dir = new File("data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            writer = new BufferedWriter(new FileWriter(Constants.TRADE_LOG_FILE, true));
            writer.write(trade.toLogString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[TradeService] File log error: " + e.getMessage());
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
            }
        }
    }

    private void serializeTrades() {
        ObjectOutputStream oos = null;
        try {
            File dir = new File("data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            oos = new ObjectOutputStream(new FileOutputStream(Constants.TRADE_SER_FILE));
            oos.writeObject(sessionTrades);
        } catch (IOException e) {
            System.err.println("[TradeService] Serialization error: " + e.getMessage());
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
            }
        }
    }

    public ArrayList<Trade> getTradeHistory(int userId) {
        return tradeDAO.findTradesByUser(userId);
    }
}
