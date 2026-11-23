package model;

public class Stock implements Displayable {
    private int stockId;
    private String symbol;
    private String companyName;
    private double currentPrice;
    private double previousClose;
    private long totalShares;
    private String sector;
    private String createdAt;

    public Stock() {
    }

    public Stock(int stockId, String symbol, String companyName, double currentPrice,
            double previousClose, long totalShares, String sector, String createdAt) {
        this.stockId = stockId;
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
        this.previousClose = previousClose;
        this.totalShares = totalShares;
        this.sector = sector;
        this.createdAt = createdAt;
    }

    public int getStockId() {
        return this.stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public double getPreviousClose() {
        return this.previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    public long getTotalShares() {
        return this.totalShares;
    }

    public void setTotalShares(long totalShares) {
        this.totalShares = totalShares;
    }

    public String getSector() {
        return this.sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getChangePercent() {
        if (this.previousClose <= 0.0) {
            return 0.0;
        }
        return ((this.currentPrice - this.previousClose) / this.previousClose) * 100.0;
    }

    @Override
    public String getDisplayHeader() {
        return String.format("%-8s %-25s %12s %12s %10s %-16s",
                "Symbol", "Company Name", "Price (INR)", "Prev Close", "Change %", "Sector");
    }

    @Override
    public String getDisplayRow() {
        String changeStr = String.format("%+.2f%%", getChangePercent());
        return String.format("%-8s %-25s %12.2f %12.2f %10s %-16s",
                this.symbol, this.companyName, this.currentPrice, this.previousClose, changeStr, this.sector);
    }

    @Override
    public String toString() {
        return this.symbol + " - " + this.companyName + " [Price: " + this.currentPrice + "]";
    }
}
