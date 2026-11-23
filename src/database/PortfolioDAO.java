package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import model.Portfolio;

public class PortfolioDAO {

    public ArrayList<Portfolio> findByUser(int userId) {
        ArrayList<Portfolio> holdings = new ArrayList<Portfolio>();
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT p.*, s.symbol, s.company_name, s.current_price "
                    + "FROM portfolios p JOIN stocks s ON p.stock_id = s.stock_id "
                    + "WHERE p.user_id = " + userId + " AND p.quantity > 0 ORDER BY s.symbol ASC";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Portfolio p = new Portfolio(
                        rs.getInt("portfolio_id"),
                        rs.getInt("user_id"),
                        rs.getInt("stock_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("avg_buy_price"));
                p.setStockSymbol(rs.getString("symbol"));
                p.setCompanyName(rs.getString("company_name"));
                p.setCurrentPrice(rs.getDouble("current_price"));
                holdings.add(p);
            }
        } catch (SQLException e) {
            System.err.println("[PortfolioDAO] Find by user error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return holdings;
    }

    public HashMap<Integer, Integer> getHoldingsMap(int userId) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT stock_id, quantity FROM portfolios WHERE user_id = " + userId;
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                map.put(Integer.valueOf(rs.getInt("stock_id")), Integer.valueOf(rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            System.err.println("[PortfolioDAO] Holdings map error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return map;
    }

    public int getHoldingQuantity(int userId, int stockId) {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT quantity FROM portfolios WHERE user_id = " + userId + " AND stock_id = " + stockId;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("quantity");
            }
        } catch (SQLException e) {
            System.err.println("[PortfolioDAO] Get qty error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return 0;
    }

    public void upsertHolding(int userId, int stockId, int quantityDelta, double tradePrice) {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String checkSql = "SELECT portfolio_id, quantity, avg_buy_price FROM portfolios "
                    + "WHERE user_id = " + userId + " AND stock_id = " + stockId;
            rs = stmt.executeQuery(checkSql);

            if (rs.next()) {
                int existingQty = rs.getInt("quantity");
                double existingAvg = rs.getDouble("avg_buy_price");
                int newQty = existingQty + quantityDelta;

                double newAvg;
                if (quantityDelta > 0 && newQty > 0) {
                    newAvg = ((existingQty * existingAvg) + (quantityDelta * tradePrice)) / newQty;
                } else if (newQty > 0) {
                    newAvg = existingAvg;
                } else {
                    newAvg = 0.0;
                }

                closeResultSet(rs);
                rs = null;

                String updateSql = "UPDATE portfolios SET quantity = " + newQty
                        + ", avg_buy_price = " + newAvg
                        + " WHERE user_id = " + userId + " AND stock_id = " + stockId;
                stmt.executeUpdate(updateSql);
            } else {
                closeResultSet(rs);
                rs = null;

                String insertSql = "INSERT INTO portfolios (user_id, stock_id, quantity, avg_buy_price) VALUES ("
                        + userId + ", " + stockId + ", " + quantityDelta + ", " + tradePrice + ")";
                stmt.executeUpdate(insertSql);
            }
        } catch (SQLException e) {
            System.err.println("[PortfolioDAO] Upsert error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
    }

    public void decreaseHolding(int userId, int stockId, int quantityToRemove) {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "UPDATE portfolios SET quantity = quantity - " + quantityToRemove
                    + " WHERE user_id = " + userId + " AND stock_id = " + stockId;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("[PortfolioDAO] Decrease error: " + e.getMessage());
        } finally {
            closeStatement(stmt);
        }
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
