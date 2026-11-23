package model;

import java.util.PriorityQueue;
import java.util.ArrayList;
import util.BuyOrderComparator;
import util.SellOrderComparator;

public class OrderBook {
    private int stockId;
    private PriorityQueue<Order> buyQueue;
    private PriorityQueue<Order> sellQueue;

    public OrderBook(int stockId) {
        this.stockId = stockId;

        this.buyQueue = new PriorityQueue<Order>(11, new BuyOrderComparator());
        this.sellQueue = new PriorityQueue<Order>(11, new SellOrderComparator());
    }

    public int getStockId() {
        return this.stockId;
    }

    public synchronized void addBuyOrder(Order order) {
        this.buyQueue.add(order);
    }

    public synchronized void addSellOrder(Order order) {
        this.sellQueue.add(order);
    }

    public synchronized Order peekBestBuy() {
        return this.buyQueue.peek();
    }

    public synchronized Order peekBestSell() {
        return this.sellQueue.peek();
    }

    public synchronized Order pollBestBuy() {
        return this.buyQueue.poll();
    }

    public synchronized Order pollBestSell() {
        return this.sellQueue.poll();
    }

    public synchronized int getBuyQueueSize() {
        return this.buyQueue.size();
    }

    public synchronized int getSellQueueSize() {
        return this.sellQueue.size();
    }

    public synchronized ArrayList<Order> getTopBuyOrders(int count) {
        ArrayList<Order> result = new ArrayList<Order>();
        PriorityQueue<Order> copy = new PriorityQueue<Order>(
                Math.max(1, this.buyQueue.size()), new BuyOrderComparator());
        copy.addAll(this.buyQueue);

        int limit = Math.min(count, copy.size());
        for (int i = 0; i < limit; i++) {
            result.add(copy.poll());
        }
        return result;
    }

    public synchronized ArrayList<Order> getTopSellOrders(int count) {
        ArrayList<Order> result = new ArrayList<Order>();
        PriorityQueue<Order> copy = new PriorityQueue<Order>(
                Math.max(1, this.sellQueue.size()), new SellOrderComparator());
        copy.addAll(this.sellQueue);

        int limit = Math.min(count, copy.size());
        for (int i = 0; i < limit; i++) {
            result.add(copy.poll());
        }
        return result;
    }
}
