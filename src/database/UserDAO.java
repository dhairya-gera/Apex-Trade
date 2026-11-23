package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;
import exception.UserNotFoundException;
import exception.DuplicateUserException;

public class UserDAO {

    public void insertUser(User user) throws DuplicateUserException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "INSERT INTO users (username, password, full_name, balance) VALUES ('"
                    + user.getUsername() + "', '"
                    + user.getPassword() + "', '"
                    + user.getFullName() + "', "
                    + user.getBalance() + ")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new DuplicateUserException("Username '" + user.getUsername() + "' is already taken.");
            }
            System.err.println("[UserDAO] Insert error: " + e.getMessage());
        } finally {
            closeStatement(stmt);
        }
    }

    public User findByUsername(String username) throws UserNotFoundException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE username = '" + username.trim() + "'";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return mapUser(rs);
            } else {
                throw new UserNotFoundException("User '" + username + "' not found.");
            }
        } catch (SQLException e) {
            throw new UserNotFoundException("Database lookup failed: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
    }

    public User findById(int userId) throws UserNotFoundException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE user_id = " + userId;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return mapUser(rs);
            } else {
                throw new UserNotFoundException("User ID #" + userId + " not found.");
            }
        } catch (SQLException e) {
            throw new UserNotFoundException("Database lookup failed: " + e.getMessage());
        } finally {
            closeResultSet(rs);
            closeStatement(stmt);
        }
    }

    public void updateBalance(int userId, double newBalance) {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "UPDATE users SET balance = " + newBalance + " WHERE user_id = " + userId;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("[UserDAO] Update balance error: " + e.getMessage());
        } finally {
            closeStatement(stmt);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getDouble("balance"),
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
