package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import model.Stock;
import exception.StockNotFoundException;

public class StockDAO {
    private LinkedHashMap<String, Stock> cache = new LinkedHashMap<String, Stock>();

    public ArrayList<Stock> findAll() {
        ArrayList<Stock> list = new ArrayList<Stock>();
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM stocks ORDER BY stock_id ASC");
            while (rs.next()) {
                Stock s = mapStock(rs);
                list.add(s);
                cache.put(s.getSymbol(), s);
            }
        } catch (SQLException e) {
            System.err.println("[StockDAO] Query error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
        return list;
    }

    public Stock findBySymbol(String symbol) throws StockNotFoundException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM stocks WHERE symbol = '" + symbol.trim().toUpperCase() + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                Stock s = mapStock(rs);
                cache.put(s.getSymbol(), s);
                return s;
            } else {
                throw new StockNotFoundException("Stock with symbol '" + symbol + "' not found.");
            }
        } catch (SQLException e) {
            throw new StockNotFoundException("Stock query error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
    }

    public Stock findById(int stockId) throws StockNotFoundException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM stocks WHERE stock_id = " + stockId;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return mapStock(rs);
            } else {
                throw new StockNotFoundException("Stock ID #" + stockId + " not found.");
            }
        } catch (SQLException e) {
            throw new StockNotFoundException("Stock query error: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
    }

    public void updatePrice(int stockId, double newPrice) {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "UPDATE stocks SET current_price = " + newPrice + " WHERE stock_id = " + stockId;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("[StockDAO] Update price error: " + e.getMessage());
        } finally {
            closeStatement(stmt);
        }
    }

    private Stock mapStock(ResultSet rs) throws SQLException {
        return new Stock(
                rs.getInt("stock_id"),
                rs.getString("symbol"),
                rs.getString("company_name"),
                rs.getDouble("current_price"),
                rs.getDouble("previous_close"),
                rs.getLong("total_shares"),
                rs.getString("sector"),
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
