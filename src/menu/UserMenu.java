package menu;

import java.util.Scanner;
import model.User;
import service.UserService;
import service.StockService;
import service.OrderService;
import service.TradeService;
import service.PortfolioService;
import thread.MatchingEngine;
import exception.UserNotFoundException;
import exception.InsufficientFundsException;

public class UserMenu extends AbstractMenu {
    private User currentUser;
    private UserService userService;
    private StockService stockService;
    private OrderService orderService;
    private TradeService tradeService;
    private PortfolioService portfolioService;
    private MatchingEngine matchingEngine;

    public UserMenu(Scanner scanner, User currentUser, UserService userService,
            StockService stockService, OrderService orderService,
            TradeService tradeService, PortfolioService portfolioService,
            MatchingEngine matchingEngine) {
        super(scanner, "TRADER DASHBOARD | " + currentUser.getFullName() + " (" + currentUser.getUsername() + ")",
                new String[] { "View Account Balance", "Add Virtual Cash Funds", "Explore Stock Market",
                        "Trade & Order Desk", "My Portfolio & Trades", "Logout" });
        this.currentUser = currentUser;
        this.userService = userService;
        this.stockService = stockService;
        this.orderService = orderService;
        this.tradeService = tradeService;
        this.portfolioService = portfolioService;
        this.matchingEngine = matchingEngine;
    }

    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1:
                showBalance();
                return true;
            case 2:
                addFunds();
                return true;
            case 3:
                new StockMenu(scanner, stockService, matchingEngine).run();
                return true;
            case 4:
                new OrderMenu(scanner, currentUser, orderService, stockService).run();
                return true;
            case 5:
                new PortfolioMenu(scanner, currentUser, portfolioService, tradeService).run();
                return true;
            case 6:
                System.out.println("  [+] Logged out from " + currentUser.getUsername() + ".");
                return false;
            default:
                System.out.println("  [!] Invalid choice. Enter 1 to 6.");
                return true;
        }
    }

    private void showBalance() {
        try {
            currentUser = userService.refreshUser(currentUser.getUserId());
            System.out.println("\n  Trader Name  : " + currentUser.getFullName());
            System.out.println("  Username     : " + currentUser.getUsername());
            System.out.println("  Cash Balance : INR " + String.format("%,.2f", currentUser.getBalance()));
        } catch (UserNotFoundException e) {
            System.out.println("  [!] Error: " + e.getMessage());
        }
    }

    private void addFunds() {
        double amount = readDoublePrompt("Enter virtual funds to deposit (INR): ");
        if (amount <= 0.0)
            return;

        try {
            userService.addFunds(currentUser.getUserId(), amount);
            currentUser = userService.refreshUser(currentUser.getUserId());
            System.out.println("  [+] Deposit successful!");
            System.out.println("  [+] Updated Cash Balance: INR " + String.format("%,.2f", currentUser.getBalance()));
        } catch (UserNotFoundException | InsufficientFundsException e) {
            System.out.println("  [!] Deposit Error: " + e.getMessage());
        }
    }
}
