package model;

public class Portfolio implements Displayable {
    private int portfolioId;
    private int userId;
    private int stockId;
    private int quantity;
    private double avgBuyPrice;

    private String stockSymbol;
    private String companyName;
    private double currentPrice;

    public Portfolio() {
    }

    public Portfolio(int portfolioId, int userId, int stockId, int quantity, double avgBuyPrice) {
        this.portfolioId = portfolioId;
        this.userId = userId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.avgBuyPrice = avgBuyPrice;
    }

    public int getPortfolioId() {
        return this.portfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        this.portfolioId = portfolioId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStockId() {
        return this.stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAvgBuyPrice() {
        return this.avgBuyPrice;
    }

    public void setAvgBuyPrice(double avgBuyPrice) {
        this.avgBuyPrice = avgBuyPrice;
    }

    public String getStockSymbol() {
        return this.stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getCurrentPrice() {
        return this.currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getInvestedValue() {
        return this.avgBuyPrice * this.quantity;
    }

    public double getCurrentValue() {
        return this.currentPrice * this.quantity;
    }

    public double getProfitLoss() {
        return getCurrentValue() - getInvestedValue();
    }

    public double getProfitLossPercent() {
        double invested = getInvestedValue();
        if (invested <= 0.0)
            return 0.0;
        return (getProfitLoss() / invested) * 100.0;
    }

    @Override
    public String getDisplayHeader() {
        return String.format("%-8s %-22s %8s %12s %12s %14s %14s %10s",
                "Symbol", "Company", "Qty", "Avg Buy", "Current", "Invested", "Current Val", "P&L %");
    }

    @Override
    public String getDisplayRow() {
        String sym = (this.stockSymbol != null) ? this.stockSymbol : String.valueOf(this.stockId);
        String comp = (this.companyName != null) ? this.companyName : "N/A";
        return String.format("%-8s %-22s %8d %12.2f %12.2f %,14.2f %,14.2f %+9.2f%%",
                sym, comp, this.quantity, this.avgBuyPrice, this.currentPrice,
                getInvestedValue(), getCurrentValue(), getProfitLossPercent());
    }

    @Override
    public String toString() {
        return "Portfolio[Stock=" + this.stockSymbol + ", Qty=" + this.quantity + ", Avg=" + this.avgBuyPrice + "]";
    }
}
