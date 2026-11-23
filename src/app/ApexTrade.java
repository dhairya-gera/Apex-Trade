package app;

import java.util.Scanner;
import java.util.ArrayList;
import database.DatabaseConnection;
import database.UserDAO;
import database.StockDAO;
import database.OrderDAO;
import database.TradeDAO;
import database.PortfolioDAO;
import service.UserService;
import service.StockService;
import service.OrderService;
import service.TradeService;
import service.PortfolioService;
import thread.OrderQueue;
import thread.MatchingEngine;
import thread.PriceFluctuationThread;
import thread.ThreadManager;
import model.Order;
import menu.MainMenu;
import util.Constants;

public class ApexTrade {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("             APEX TRADE - PAPER STOCK EXCHANGE                    ");
        System.out.println("          Virtual Automated Electronic Stock Market               ");
        System.out.println("==================================================================");

        if (DatabaseConnection.getConnection() == null) {
            System.err.println("[FATAL] Cannot establish connection with MySQL Database.");
            System.err.println("[FATAL] Please check database credentials in util/Constants.java.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        StockDAO stockDAO = new StockDAO();
        OrderDAO orderDAO = new OrderDAO();
        TradeDAO tradeDAO = new TradeDAO();
        PortfolioDAO portfolioDAO = new PortfolioDAO();

        UserService userService = new UserService(userDAO);
        StockService stockService = new StockService(stockDAO);
        PortfolioService portfolioService = new PortfolioService(portfolioDAO, stockDAO);
        TradeService tradeService = new TradeService(tradeDAO, userDAO, portfolioDAO, orderDAO, stockDAO);

        OrderQueue orderQueue = new OrderQueue(Constants.ORDER_QUEUE_CAPACITY);
        OrderService orderService = new OrderService(orderDAO, userDAO, portfolioDAO, stockDAO, orderQueue);

        MatchingEngine matchingEngine = new MatchingEngine(orderQueue, tradeService);
        PriceFluctuationThread priceThread = new PriceFluctuationThread(stockDAO);

        ArrayList<Order> openOrders = orderDAO.findAllOpenOrders();
        for (int i = 0; i < openOrders.size(); i++) {
            matchingEngine.addToOrderBook(openOrders.get(i));
        }
        if (!openOrders.isEmpty()) {
            System.out.println("[BOOT] Loaded " + openOrders.size() + " active orders into live order books.");
        }

        ThreadManager threadManager = new ThreadManager(matchingEngine, priceThread);
        threadManager.startAll();

        Scanner scanner = new Scanner(System.in);
        MainMenu mainMenu = new MainMenu(scanner, userService, stockService,
                orderService, tradeService, portfolioService, matchingEngine);

        try {
            mainMenu.run();
        } catch (Exception e) {
            System.err.println("[CRITICAL] Unhandled application error: " + e.getMessage());
            e.printStackTrace();
        } finally {

            threadManager.shutdownAll();
            DatabaseConnection.closeConnection();
            scanner.close();
            System.out.println("\n[EXIT] System terminated cleanly. Good bye!");
        }
    }
}
