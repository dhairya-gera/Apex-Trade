package thread;

import java.util.ArrayList;
import java.util.HashMap;
import model.Order;
import model.OrderBook;
import service.TradeService;
import util.Constants;

public class MatchingEngine implements Runnable {
    private OrderQueue orderQueue;
    private TradeService tradeService;
    private HashMap<Integer, OrderBook> orderBooks;

    public MatchingEngine(OrderQueue orderQueue, TradeService tradeService) {
        this.orderQueue = orderQueue;
        this.tradeService = tradeService;
        this.orderBooks = new HashMap<Integer, OrderBook>();
    }

    public OrderBook getOrderBook(int stockId) {
        synchronized (orderBooks) {
            Integer key = Integer.valueOf(stockId);
            if (!orderBooks.containsKey(key)) {
                orderBooks.put(key, new OrderBook(stockId));
            }
            return orderBooks.get(key);
        }
    }

    public void addToOrderBook(Order order) {
        OrderBook book = getOrderBook(order.getStockId());
        if (Constants.ORDER_TYPE_BUY.equals(order.getOrderType())) {
            book.addBuyOrder(order);
        } else {
            book.addSellOrder(order);
        }
    }

    @Override
    public void run() {
        System.out.println("[ENGINE] MatchingEngine thread active.");
        matchExisting();

        while (MarketState.isOpen()) {
            try {
                Order order = orderQueue.consume();
                if (order == null) {
                    continue;
                }
                addToOrderBook(order);
                matchOrdersForStock(order.getStockId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("[ENGINE] MatchingEngine thread terminated.");
    }

    public void matchOrdersForStock(int stockId) {
        OrderBook book = getOrderBook(stockId);

        while (true) {
            Order bestBuy;
            Order bestSell;

            synchronized (book) {
                bestBuy = book.peekBestBuy();
                bestSell = book.peekBestSell();
            }

            if (bestBuy == null || bestSell == null) {
                break;
            }

            if (bestBuy.getPrice() < bestSell.getPrice()) {
                break;
            }

            synchronized (book) {
                bestBuy = book.pollBestBuy();
                bestSell = book.pollBestSell();
            }

            if (bestBuy == null || bestSell == null) {
                break;
            }

            int buyRem = bestBuy.getRemainingQuantity();
            int sellRem = bestSell.getRemainingQuantity();
            int tradeQty = Math.min(buyRem, sellRem);
            double execPrice = bestSell.getPrice();

            bestBuy.setFilledQuantity(bestBuy.getFilledQuantity() + tradeQty);
            bestSell.setFilledQuantity(bestSell.getFilledQuantity() + tradeQty);

            if (bestBuy.getFilledQuantity() >= bestBuy.getQuantity()) {
                bestBuy.setStatus(Constants.STATUS_FILLED);
            } else {
                bestBuy.setStatus(Constants.STATUS_PARTIAL);
                book.addBuyOrder(bestBuy);
            }

            if (bestSell.getFilledQuantity() >= bestSell.getQuantity()) {
                bestSell.setStatus(Constants.STATUS_FILLED);
            } else {
                bestSell.setStatus(Constants.STATUS_PARTIAL);
                book.addSellOrder(bestSell);
            }

            tradeService.executeTrade(bestBuy, bestSell, tradeQty, execPrice);
        }
    }

    private void matchExisting() {
        synchronized (orderBooks) {
            ArrayList<Integer> keys = new ArrayList<Integer>(orderBooks.keySet());
            for (int i = 0; i < keys.size(); i++) {
                matchOrdersForStock(keys.get(i).intValue());
            }
        }
    }
}
