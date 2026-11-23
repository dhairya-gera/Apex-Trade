package thread;

import java.util.LinkedList;
import model.Order;

public class OrderQueue {
    private LinkedList<Order> queue;
    private int capacity;

    public OrderQueue(int capacity) {
        this.queue = new LinkedList<Order>();
        this.capacity = capacity;
    }

    public synchronized void produce(Order order) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        queue.addLast(order);
        notifyAll();
    }

    public synchronized Order consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(1500);
            if (!MarketState.isOpen() && queue.isEmpty()) {
                return null;
            }
        }
        Order order = queue.removeFirst();
        notifyAll();
        return order;
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}
