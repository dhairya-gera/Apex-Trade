package model;

import java.io.Serializable;

public class Trade implements Displayable, Serializable {
    private static final long serialVersionUID = 2L;

    private int tradeId;
    private int buyOrderId;
    private int sellOrderId;
    private int stockId;
    private int buyerId;
    private int sellerId;
    private double price;
    private int quantity;
    private String executedAt;

    private String stockSymbol;
    private String buyerName;
    private String sellerName;

    public Trade() {
    }

    public Trade(int tradeId, int buyOrderId, int sellOrderId, int stockId,
            int buyerId, int sellerId, double price, int quantity, String executedAt) {
        this.tradeId = tradeId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.stockId = stockId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.price = price;
        this.quantity = quantity;
        this.executedAt = executedAt;
    }

    public Trade(int buyOrderId, int sellOrderId, int stockId,
            int buyerId, int sellerId, double price, int quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.stockId = stockId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.price = price;
        this.quantity = quantity;
    }

    public int getTradeId() {
        return this.tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public int getBuyOrderId() {
        return this.buyOrderId;
    }

    public int getSellOrderId() {
        return this.sellOrderId;
    }

    public int getStockId() {
        return this.stockId;
    }

    public int getBuyerId() {
        return this.buyerId;
    }

    public int getSellerId() {
        return this.sellerId;
    }

    public double getPrice() {
        return this.price;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getExecutedAt() {
        return this.executedAt;
    }

    public void setExecutedAt(String executedAt) {
        this.executedAt = executedAt;
    }

    public String getStockSymbol() {
        return this.stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getBuyerName() {
        return this.buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerName() {
        return this.sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public double getTotalValue() {
        return this.price * this.quantity;
    }

    @Override
    public String getDisplayHeader() {
        return String.format("%-8s %-8s %-14s %-14s %12s %8s %16s %-20s",
                "Trade ID", "Stock", "Buyer", "Seller", "Exec Price", "Qty", "Total Value", "Execution Time");
    }

    @Override
    public String getDisplayRow() {
        String sym = (this.stockSymbol != null) ? this.stockSymbol : String.valueOf(this.stockId);
        String bName = (this.buyerName != null) ? this.buyerName : ("User #" + this.buyerId);
        String sName = (this.sellerName != null) ? this.sellerName : ("User #" + this.sellerId);
        return String.format("%-8d %-8s %-14s %-14s %12.2f %8d %,16.2f %-20s",
                this.tradeId, sym, bName, sName, this.price, this.quantity, getTotalValue(),
                (this.executedAt != null) ? this.executedAt : "N/A");
    }

    public String toLogString() {
        return "TRADE #" + this.tradeId + " | Symbol: " + (this.stockSymbol != null ? this.stockSymbol : this.stockId)
                + " | Price: " + String.format("%.2f", this.price)
                + " | Qty: " + this.quantity
                + " | Buyer ID: " + this.buyerId
                + " | Seller ID: " + this.sellerId
                + " | Time: " + this.executedAt;
    }

    @Override
    public String toString() {
        return toLogString();
    }
}
