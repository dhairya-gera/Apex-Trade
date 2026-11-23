package service;

import database.OrderDAO;
import database.UserDAO;
import database.PortfolioDAO;
import database.StockDAO;
import model.Order;
import model.User;
import model.Stock;
import thread.OrderQueue;
import exception.InsufficientFundsException;
import exception.InsufficientSharesException;
import exception.InvalidOrderException;
import exception.UserNotFoundException;
import exception.StockNotFoundException;
import util.Constants;
import java.util.ArrayList;

public class OrderService {
    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private PortfolioDAO portfolioDAO;
    private StockDAO stockDAO;
    private OrderQueue orderQueue;

    public OrderService(OrderDAO orderDAO, UserDAO userDAO, PortfolioDAO portfolioDAO,
            StockDAO stockDAO, OrderQueue orderQueue) {
        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
        this.portfolioDAO = portfolioDAO;
        this.stockDAO = stockDAO;
        this.orderQueue = orderQueue;
    }

    public Order placeBuyOrder(int userId, String symbol, double price, int quantity)
            throws InvalidOrderException, InsufficientFundsException, UserNotFoundException, StockNotFoundException {

        if (price <= 0.0) {
            throw new InvalidOrderException("Limit buy price must be positive. Given: " + price);
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("Order quantity must be positive. Given: " + quantity);
        }

        Stock stock = stockDAO.findBySymbol(symbol);
        double totalRequiredFunds = price * quantity;

        synchronized (Constants.TRADE_LOCK) {
            User user = userDAO.findById(userId);
            if (user.getBalance() < totalRequiredFunds) {
                throw new InsufficientFundsException(totalRequiredFunds, user.getBalance());
            }

            userDAO.updateBalance(userId, user.getBalance() - totalRequiredFunds);

            Order order = new Order(userId, stock.getStockId(), Constants.ORDER_TYPE_BUY, price, quantity);
            int generatedId = orderDAO.insertOrder(order);
            order.setOrderId(generatedId);
            order.setStockSymbol(stock.getSymbol());

            try {
                orderQueue.produce(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return order;
        }
    }

    public Order placeSellOrder(int userId, String symbol, double price, int quantity)
            throws InvalidOrderException, InsufficientSharesException, UserNotFoundException, StockNotFoundException {

        if (price <= 0.0) {
            throw new InvalidOrderException("Limit sell price must be positive. Given: " + price);
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("Order quantity must be positive. Given: " + quantity);
        }

        Stock stock = stockDAO.findBySymbol(symbol);

        synchronized (Constants.TRADE_LOCK) {
            int currentHolding = portfolioDAO.getHoldingQuantity(userId, stock.getStockId());
            if (currentHolding < quantity) {
                throw new InsufficientSharesException(quantity, currentHolding);
            }

            portfolioDAO.decreaseHolding(userId, stock.getStockId(), quantity);

            Order order = new Order(userId, stock.getStockId(), Constants.ORDER_TYPE_SELL, price, quantity);
            int generatedId = orderDAO.insertOrder(order);
            order.setOrderId(generatedId);
            order.setStockSymbol(stock.getSymbol());

            try {
                orderQueue.produce(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return order;
        }
    }

    public ArrayList<Order> getUserOrders(int userId) {
        return orderDAO.findOrdersByUser(userId);
    }
}
