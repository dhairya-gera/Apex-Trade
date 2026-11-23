package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import model.Order;

public class OrderDAO {

    public int insertOrder(Order order) {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        int generatedId = -1;
        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO orders (user_id, stock_id, order_type, price, quantity, filled_quantity, status) VALUES ("
                    + order.getUserId() + ", "
                    + order.getStockId() + ", '"
                    + order.getOrderType() + "', "
                    + order.getPrice() + ", "
                    + order.getQuantity() + ", "
                    + order.getFilledQuantity() + ", '"
                    + order.getStatus() + "')";
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Insert error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return generatedId;
    }

    public void updateOrder(int orderId, int filledQuantity, String status) {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "UPDATE orders SET filled_quantity = " + filledQuantity
                    + ", status = '" + status + "' WHERE order_id = " + orderId;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Update error: " + e.getMessage());
        } finally {
            closeStatement(stmt);
        }
    }

    public LinkedList<Order> findOpenOrdersByStock(int stockId) {
        LinkedList<Order> orders = new LinkedList<Order>();
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT o.*, s.symbol FROM orders o JOIN stocks s ON o.stock_id = s.stock_id "
                    + "WHERE o.stock_id = " + stockId
                    + " AND (o.status = 'OPEN' OR o.status = 'PARTIAL') ORDER BY o.created_at ASC";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Order order = mapOrder(rs);
                order.setStockSymbol(rs.getString("symbol"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Query error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return orders;
    }

    public ArrayList<Order> findAllOpenOrders() {
        ArrayList<Order> list = new ArrayList<Order>();
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT o.*, s.symbol FROM orders o JOIN stocks s ON o.stock_id = s.stock_id "
                    + "WHERE o.status = 'OPEN' OR o.status = 'PARTIAL' ORDER BY o.created_at ASC";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Order order = mapOrder(rs);
                order.setStockSymbol(rs.getString("symbol"));
                list.add(order);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Query all open orders error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return list;
    }

    public ArrayList<Order> findOrdersByUser(int userId) {
        ArrayList<Order> list = new ArrayList<Order>();
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT o.*, s.symbol FROM orders o JOIN stocks s ON o.stock_id = s.stock_id "
                    + "WHERE o.user_id = " + userId + " ORDER BY o.created_at DESC";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Order order = mapOrder(rs);
                order.setStockSymbol(rs.getString("symbol"));
                list.add(order);
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] User orders query error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return list;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("order_id"),
                rs.getInt("user_id"),
                rs.getInt("stock_id"),
                rs.getString("order_type"),
                rs.getDouble("price"),
                rs.getInt("quantity"),
                rs.getInt("filled_quantity"),
                rs.getString("status"),
                rs.getString("created_at"));
    }

    private void closeStatement(Statement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
        }
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
        }
    }
}
