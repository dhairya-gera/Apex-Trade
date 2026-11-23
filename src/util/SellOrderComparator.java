package util;

import java.util.Comparator;
import model.Order;

public class SellOrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order o1, Order o2) {
        int priceComp = Double.compare(o1.getPrice(), o2.getPrice());
        if (priceComp != 0) {
            return priceComp;
        }
        if (o1.getCreatedAt() != null && o2.getCreatedAt() != null) {
            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
        }
        return Integer.compare(o1.getOrderId(), o2.getOrderId());
    }
}
