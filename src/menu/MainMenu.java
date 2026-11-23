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
import exception.DuplicateUserException;
import util.InputValidator;

public class MainMenu extends AbstractMenu {
    private UserService userService;
    private StockService stockService;
    private OrderService orderService;
    private TradeService tradeService;
    private PortfolioService portfolioService;
    private MatchingEngine matchingEngine;

    public MainMenu(Scanner scanner, UserService userService, StockService stockService,
            OrderService orderService, TradeService tradeService,
            PortfolioService portfolioService, MatchingEngine matchingEngine) {
        super(scanner, "APEX TRADE - Paper Stock Exchange",
                new String[] { "Register New Account", "User Login", "Exit" });
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
                doRegister();
                return true;
            case 2:
                doLogin();
                return true;
            case 3:
                System.out.println("\n  Shutting down Apex Trade. Have a profitable day!");
                return false;
            default:
                System.out.println("  [!] Invalid choice. Select 1 to 3.");
                return true;
        }
    }

    private void doRegister() {
        System.out.println("\n--- [Account Registration] ---");
        String username = readPrompt("Desired Username (min 3 chars): ");
        if (!InputValidator.isValidUsername(username)) {
            System.out.println("  [!] Username must be between 3 and 50 characters.");
            return;
        }

        String password = readPrompt("Password (min 3 chars): ");
        if (!InputValidator.isValidPassword(password)) {
            System.out.println("  [!] Password must be at least 3 characters.");
            return;
        }

        String fullName = readPrompt("Full Legal Name: ");
        if (!InputValidator.isNonEmptyString(fullName)) {
            System.out.println("  [!] Full Name cannot be empty.");
            return;
        }

        try {
            User registered = userService.register(username, password, fullName);
            System.out.println("  [+] Registration successful! Welcome, " + registered.getFullName() + ".");
            System.out.println(
                    "  [+] Starting virtual cash credited: INR " + String.format("%,.2f", registered.getBalance()));
        } catch (DuplicateUserException e) {
            System.out.println("  [!] Registration Failed: " + e.getMessage());
        }
    }

    private void doLogin() {
        System.out.println("\n--- [Trader Login] ---");
        String username = readPrompt("Username: ");
        String password = readPrompt("Password: ");

        try {
            User user = userService.login(username, password);
            System.out.println("  [+] Authentication successful. Welcome back, " + user.getFullName() + "!");

            UserMenu userMenu = new UserMenu(scanner, user, userService, stockService,
                    orderService, tradeService, portfolioService, matchingEngine);
            userMenu.run();

        } catch (UserNotFoundException e) {
            System.out.println("  [!] Login Failed: " + e.getMessage());
        }
    }
}
