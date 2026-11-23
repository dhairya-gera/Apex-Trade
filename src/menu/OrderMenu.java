package menu;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.ListIterator;
import model.User;
import model.Order;
import service.OrderService;
import service.StockService;
import exception.InsufficientFundsException;
import exception.InsufficientSharesException;
import exception.InvalidOrderException;
import exception.UserNotFoundException;
import exception.StockNotFoundException;
import util.InputValidator;

public class OrderMenu extends AbstractMenu {
    private User currentUser;
    private OrderService orderService;
    private StockService stockService;

    public OrderMenu(Scanner scanner, User currentUser, OrderService orderService, StockService stockService) {
        super(scanner, "TRADE & ORDER DESK",
                new String[] { "Place Limit BUY Order", "Place Limit SELL Order", "View My Order History",
                        "Back to Dashboard" });
        this.currentUser = currentUser;
        this.orderService = orderService;
        this.stockService = stockService;
    }

    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1:
                placeBuy();
                return true;
            case 2:
                placeSell();
                return true;
            case 3:
                viewOrders();
                return true;
            case 4:
                return false;
            default:
                System.out.println("  [!] Invalid choice. Enter 1 to 4.");
                return true;
        }
    }

    private void placeBuy() {
        System.out.println("\n--- [Place Limit BUY Order] ---");
        String symbol = readPrompt("Stock Symbol (e.g., APEX, NOVA, ZENT): ");
        if (!InputValidator.isValidStockSymbol(symbol)) {
            System.out.println("  [!] Invalid or unknown stock symbol.");
            return;
        }

        double price = readDoublePrompt("Enter Limit Buy Price per share (INR): ");
        if (price <= 0.0)
            return;

        int quantity = readIntPrompt("Enter Quantity of shares: ");
        if (quantity <= 0)
            return;

        try {
            Order order = orderService.placeBuyOrder(currentUser.getUserId(), symbol, price, quantity);
            System.out.println("  [+] Limit BUY Order successfully placed!");
            System.out.println("  [+] Order ID: #" + order.getOrderId() + " | " + symbol.toUpperCase() + " x "
                    + quantity + " @ INR " + String.format("%.2f", price));
            System.out.println("  [+] Cash reserved: INR " + String.format("%,.2f", (price * quantity)));
        } catch (InvalidOrderException | InsufficientFundsException | UserNotFoundException
                | StockNotFoundException e) {
            System.out.println("  [!] Order Rejected: " + e.getMessage());
        }
    }

    private void placeSell() {
        System.out.println("\n--- [Place Limit SELL Order] ---");
        String symbol = readPrompt("Stock Symbol to Sell: ");
        if (!InputValidator.isValidStockSymbol(symbol)) {
            System.out.println("  [!] Invalid or unknown stock symbol.");
            return;
        }

        double price = readDoublePrompt("Enter Limit Sell Price per share (INR): ");
        if (price <= 0.0)
            return;

        int quantity = readIntPrompt("Enter Quantity of shares to sell: ");
        if (quantity <= 0)
            return;

        try {
            Order order = orderService.placeSellOrder(currentUser.getUserId(), symbol, price, quantity);
            System.out.println("  [+] Limit SELL Order successfully placed!");
            System.out.println("  [+] Order ID: #" + order.getOrderId() + " | " + symbol.toUpperCase() + " x "
                    + quantity + " @ INR " + String.format("%.2f", price));
            System.out.println("  [+] Shares locked into order book queue.");
        } catch (InvalidOrderException | InsufficientSharesException | UserNotFoundException
                | StockNotFoundException e) {
            System.out.println("  [!] Order Rejected: " + e.getMessage());
        }
    }

    private void viewOrders() {
        ArrayList<Order> orders = orderService.getUserOrders(currentUser.getUserId());
        System.out.println("\n--- [My Order History] ---");
        if (orders.isEmpty()) {
            System.out.println("  You haven't placed any orders yet.");
            return;
        }

        System.out.println(orders.get(0).getDisplayHeader());
        System.out.println("-----------------------------------------------------------------------------------------");
        ListIterator<Order> it = orders.listIterator();
        while (it.hasNext()) {
            System.out.println(it.next().getDisplayRow());
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("  Total Orders Placed: " + orders.size());
    }
}
