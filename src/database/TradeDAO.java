package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Trade;

public class TradeDAO {

    public int insertTrade(Trade trade) {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        int generatedId = -1;
        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO trades (buy_order_id, sell_order_id, stock_id, buyer_id, seller_id, price, quantity) VALUES ("
                    + trade.getBuyOrderId() + ", "
                    + trade.getSellOrderId() + ", "
                    + trade.getStockId() + ", "
                    + trade.getBuyerId() + ", "
                    + trade.getSellerId() + ", "
                    + trade.getPrice() + ", "
                    + trade.getQuantity() + ")";
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[TradeDAO] Insert trade error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return generatedId;
    }

    public ArrayList<Trade> findTradesByUser(int userId) {
        ArrayList<Trade> list = new ArrayList<Trade>();
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT t.*, s.symbol, b.username AS buyer_name, sl.username AS seller_name "
                    + "FROM trades t "
                    + "JOIN stocks s ON t.stock_id = s.stock_id "
                    + "JOIN users b ON t.buyer_id = b.user_id "
                    + "JOIN users sl ON t.seller_id = sl.user_id "
                    + "WHERE t.buyer_id = " + userId + " OR t.seller_id = " + userId
                    + " ORDER BY t.executed_at DESC";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Trade t = new Trade(
                        rs.getInt("trade_id"),
                        rs.getInt("buy_order_id"),
                        rs.getInt("sell_order_id"),
                        rs.getInt("stock_id"),
                        rs.getInt("buyer_id"),
                        rs.getInt("seller_id"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("executed_at"));
                t.setStockSymbol(rs.getString("symbol"));
                t.setBuyerName(rs.getString("buyer_name"));
                t.setSellerName(rs.getString("seller_name"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("[TradeDAO] Query trades error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return list;
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
