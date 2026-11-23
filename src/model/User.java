package model;

import java.io.Serializable;

public class User implements Displayable, Serializable, Comparable<User> {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private double balance;
    private String createdAt;

    public User() {
    }

    public User(int userId, String username, String password, String fullName, double balance, String createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public User(String username, String password, String fullName, double balance) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.balance = balance;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int compareTo(User other) {
        return this.username.compareTo(other.username);
    }

    @Override
    public String getDisplayHeader() {
        return String.format("%-8s %-15s %-22s %16s", "User ID", "Username", "Full Name", "Cash Balance");
    }

    @Override
    public String getDisplayRow() {
        return String.format("%-8d %-15s %-22s %,16.2f", this.userId, this.username, this.fullName, this.balance);
    }

    @Override
    public String toString() {
        return "User[ID=" + this.userId + ", username=" + this.username + ", balance=" + this.balance + "]";
    }
}
