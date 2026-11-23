package service;

import database.UserDAO;
import model.User;
import exception.UserNotFoundException;
import exception.DuplicateUserException;
import exception.InsufficientFundsException;
import util.Constants;

public class UserService {
    private UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String username, String password, String fullName) throws DuplicateUserException {
        User user = new User(username, password, fullName, Constants.DEFAULT_BALANCE);
        userDAO.insertUser(user);
        try {
            return userDAO.findByUsername(username);
        } catch (UserNotFoundException e) {
            return user;
        }
    }

    public User login(String username, String password) throws UserNotFoundException {
        User user = userDAO.findByUsername(username);

        if (!user.getPassword().equals(password)) {
            throw new UserNotFoundException("Invalid password for user '" + username + "'.");
        }
        return user;
    }

    public User refreshUser(int userId) throws UserNotFoundException {
        return userDAO.findById(userId);
    }

    public void addFunds(int userId, double amount) throws UserNotFoundException, InsufficientFundsException {
        if (amount <= 0.0) {
            throw new InsufficientFundsException("Deposit amount must be positive. Received: " + amount);
        }
        User user = userDAO.findById(userId);
        double updatedBalance = user.getBalance() + amount;
        userDAO.updateBalance(userId, updatedBalance);
    }
}
