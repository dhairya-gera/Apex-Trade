package util;

import java.util.Comparator;
import model.Order;

public class BuyOrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order o1, Order o2) {
        int priceComp = Double.compare(o2.getPrice(), o1.getPrice());
        if (priceComp != 0) {
            return priceComp;
        }
        if (o1.getCreatedAt() != null && o2.getCreatedAt() != null) {
            return o1.getCreatedAt().compareTo(o2.getCreatedAt());
        }
        return Integer.compare(o1.getOrderId(), o2.getOrderId());
    }
}
