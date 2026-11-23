package model;

public class Order implements Displayable, Comparable<Order> {
    private int orderId;
    private int userId;
    private int stockId;
    private String orderType;
    private double price;
    private int quantity;
    private int filledQuantity;
    private String status;
    private String createdAt;
    private String stockSymbol;

    public Order() {
    }

    public Order(int orderId, int userId, int stockId, String orderType,
            double price, int quantity, int filledQuantity, String status, String createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.stockId = stockId;
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
        this.filledQuantity = filledQuantity;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Order(int userId, int stockId, String orderType, double price, int quantity) {
        this.userId = userId;
        this.stockId = stockId;
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
        this.filledQuantity = 0;
        this.status = "OPEN";
    }

    public int getOrderId() {
        return this.orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public String getOrderType() {
        return this.orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFilledQuantity() {
        return this.filledQuantity;
    }

    public void setFilledQuantity(int filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStockSymbol() {
        return this.stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public int getRemainingQuantity() {
        return this.quantity - this.filledQuantity;
    }

    @Override
    public int compareTo(Order other) {

        return Integer.compare(this.orderId, other.orderId);
    }

    @Override
    public String getDisplayHeader() {
        return String.format("%-8s %-8s %-6s %12s %8s %8s %-10s %-20s",
                "Order ID", "Stock", "Type", "Price", "Qty", "Filled", "Status", "Placed At");
    }

    @Override
    public String getDisplayRow() {
        String sym = (this.stockSymbol != null) ? this.stockSymbol : String.valueOf(this.stockId);
        return String.format("%-8d %-8s %-6s %12.2f %8d %8d %-10s %-20s",
                this.orderId, sym, this.orderType, this.price,
                this.quantity, this.filledQuantity, this.status,
                (this.createdAt != null) ? this.createdAt : "N/A");
    }

    @Override
    public String toString() {
        return "Order #" + this.orderId + " [" + this.orderType + " " + this.quantity + " @ " + this.price + ", Status="
                + this.status + "]";
    }
}
