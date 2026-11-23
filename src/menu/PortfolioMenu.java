package menu;

import java.util.Scanner;
import java.util.ArrayList;
import model.User;
import model.Portfolio;
import model.Trade;
import service.PortfolioService;
import service.TradeService;
import util.TableFormatter;

public class PortfolioMenu extends AbstractMenu {
    private User currentUser;
    private PortfolioService portfolioService;
    private TradeService tradeService;

    public PortfolioMenu(Scanner scanner, User currentUser, PortfolioService portfolioService,
            TradeService tradeService) {
        super(scanner, "PORTFOLIO & TRADE AUDIT",
                new String[] { "View Stock Holdings & P&L", "View Executed Trade Log", "Back to Dashboard" });
        this.currentUser = currentUser;
        this.portfolioService = portfolioService;
        this.tradeService = tradeService;
    }

    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1:
                viewHoldings();
                return true;
            case 2:
                viewTrades();
                return true;
            case 3:
                return false;
            default:
                System.out.println("  [!] Invalid choice. Enter 1 to 3.");
                return true;
        }
    }

    private void viewHoldings() {
        ArrayList<Portfolio> holdings = portfolioService.getUserPortfolio(currentUser.getUserId());
        System.out.println("\n--- [Active Stock Holdings] ---");
        if (holdings.isEmpty()) {
            System.out.println("  You currently hold zero shares in any company.");
            return;
        }

        TableFormatter.printTable(holdings);

        double totalInvested = portfolioService.getTotalInvestedValue(currentUser.getUserId());
        double totalCurrent = portfolioService.getTotalPortfolioValue(currentUser.getUserId());
        double totalPnL = portfolioService.getTotalProfitLoss(currentUser.getUserId());
        double pnlPct = (totalInvested > 0.0) ? (totalPnL / totalInvested * 100.0) : 0.0;

        System.out.println(String.format("  Total Invested Capital : INR %,15.2f", totalInvested));
        System.out.println(String.format("  Current Market Value   : INR %,15.2f", totalCurrent));
        System.out.println(String.format("  Total Net Unrealized   : INR %+,15.2f (%+.2f%%)", totalPnL, pnlPct));
    }

    private void viewTrades() {
        ArrayList<Trade> trades = tradeService.getTradeHistory(currentUser.getUserId());
        System.out.println("\n--- [Executed Trade Settlement History] ---");
        if (trades.isEmpty()) {
            System.out.println("  No matched trades recorded for your account yet.");
            return;
        }

        TableFormatter.printTable(trades);
        System.out.println("  Total Trades Settled: " + trades.size());
    }
}
