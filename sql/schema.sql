-- ==========================================================
-- APEX TRADE - Paper Stock Exchange
-- Database Schema and Seed Data
-- ==========================================================
-- Run this file in MySQL:  mysql -u root -p < schema.sql
-- ==========================================================

DROP DATABASE IF EXISTS apex_trade;
CREATE DATABASE apex_trade;
USE apex_trade;

-- -----------------------------------------------------------
-- TABLE: users
-- Stores registered trader accounts and their cash balance.
-- -----------------------------------------------------------
CREATE TABLE users (
    user_id        INT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password       VARCHAR(100) NOT NULL,
    full_name      VARCHAR(100) NOT NULL,
    balance        DOUBLE       NOT NULL DEFAULT 100000.00,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------
-- TABLE: stocks
-- Master list of tradeable companies.
-- previous_close is kept for daily change calculation.
-- -----------------------------------------------------------
CREATE TABLE stocks (
    stock_id       INT AUTO_INCREMENT PRIMARY KEY,
    symbol         VARCHAR(10)  NOT NULL UNIQUE,
    company_name   VARCHAR(100) NOT NULL,
    current_price  DOUBLE       NOT NULL,
    previous_close DOUBLE       NOT NULL,
    total_shares   BIGINT       NOT NULL,
    sector         VARCHAR(50)  NOT NULL,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------
-- TABLE: orders
-- Every buy/sell limit order placed by a user.
-- status: OPEN | PARTIAL | FILLED | CANCELLED
-- order_type: BUY | SELL
-- -----------------------------------------------------------
CREATE TABLE orders (
    order_id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT         NOT NULL,
    stock_id        INT         NOT NULL,
    order_type      VARCHAR(4)  NOT NULL,
    price           DOUBLE      NOT NULL,
    quantity        INT         NOT NULL,
    filled_quantity INT         NOT NULL DEFAULT 0,
    status          VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)  REFERENCES users(user_id),
    FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);

-- -----------------------------------------------------------
-- TABLE: trades
-- Each row = one matched execution between a buyer and seller.
-- -----------------------------------------------------------
CREATE TABLE trades (
    trade_id       INT AUTO_INCREMENT PRIMARY KEY,
    buy_order_id   INT       NOT NULL,
    sell_order_id  INT       NOT NULL,
    stock_id       INT       NOT NULL,
    buyer_id       INT       NOT NULL,
    seller_id      INT       NOT NULL,
    price          DOUBLE    NOT NULL,
    quantity       INT       NOT NULL,
    executed_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buy_order_id)  REFERENCES orders(order_id),
    FOREIGN KEY (sell_order_id) REFERENCES orders(order_id),
    FOREIGN KEY (stock_id)      REFERENCES stocks(stock_id),
    FOREIGN KEY (buyer_id)      REFERENCES users(user_id),
    FOREIGN KEY (seller_id)     REFERENCES users(user_id)
);

-- -----------------------------------------------------------
-- TABLE: portfolios
-- Current share holdings per user per stock.
-- avg_buy_price tracks the weighted average purchase price.
-- -----------------------------------------------------------
CREATE TABLE portfolios (
    portfolio_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id        INT    NOT NULL,
    stock_id       INT    NOT NULL,
    quantity       INT    NOT NULL DEFAULT 0,
    avg_buy_price  DOUBLE NOT NULL DEFAULT 0.0,
    UNIQUE KEY uk_user_stock (user_id, stock_id),
    FOREIGN KEY (user_id)  REFERENCES users(user_id),
    FOREIGN KEY (stock_id) REFERENCES stocks(stock_id)
);


-- ==========================================================
-- SEED DATA: 8 Fake Companies (diverse sectors)
-- ==========================================================
INSERT INTO stocks (symbol, company_name, current_price, previous_close, total_shares, sector) VALUES
('APEX', 'Apex Technologies Ltd',    152.40, 150.00, 1000000, 'Technology'),
('NOVA', 'Nova Energy Corp',          87.25,  85.50,  500000, 'Energy'),
('ZENT', 'Zenith Pharmaceuticals',   225.80, 220.00,  750000, 'Healthcare'),
('BOLT', 'Bolt Motors India',         98.50,  95.75,  600000, 'Automotive'),
('SKYP', 'SkyPulse Airlines',         44.10,  42.30,  800000, 'Aviation'),
('CRST', 'CrestBank Holdings',      185.00, 180.00,  400000, 'Banking'),
('NXGN', 'NexGen Robotics',         318.75, 310.50,  300000, 'Technology'),
('GRNA', 'GreenArc Foods Ltd',       67.50,  65.00,  900000, 'FMCG');

-- ==========================================================
-- SEED DATA: 2 Sample Users (for quick demo/testing)
-- Passwords are plain text — acceptable for a college demo.
-- ==========================================================
INSERT INTO users (username, password, full_name, balance) VALUES
('alice', 'alice123', 'Alice Sharma', 500000.00),
('bob',   'bob123',   'Bob Patel',   500000.00);

-- ==========================================================
-- Give Alice and Bob some initial holdings so the demo
-- has sellers from the start.
-- ==========================================================
INSERT INTO portfolios (user_id, stock_id, quantity, avg_buy_price) VALUES
(1, 1, 200, 148.00),   -- Alice holds 200 APEX
(1, 3, 100, 218.50),   -- Alice holds 100 ZENT
(1, 5, 500, 40.00),    -- Alice holds 500 SKYP
(2, 2, 300, 83.00),    -- Bob   holds 300 NOVA
(2, 4, 150, 94.00),    -- Bob   holds 150 BOLT
(2, 7, 100, 305.00);   -- Bob   holds 100 NXGN
