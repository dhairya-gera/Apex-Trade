# APEX TRADE — Paper Stock Exchange (CA-3 Project)

A complete, robust, multi-threaded virtual stock exchange developed in core Java using direct MySQL JDBC integration.

---

## 1. Setup & Execution Guide

### 1.1 Prerequisites
1. **JDK 8 or higher** (Tested on JDK 17, 21, and 26).
2. **MySQL Server** (Running locally on default port `3306`).
3. **MySQL Connector/J JAR** (`mysql-connector-j-9.1.0.jar` or any `mysql-connector-j-8.x.x.jar`).

---

### 1.2 Step 1: Database Setup
1. Open MySQL Command Line Client or MySQL Workbench.
2. Run the provided schema script:
```bash
mysql -u root -p < "sql/schema.sql"
```
Or inside the MySQL CLI:
```sql
SOURCE sql/schema.sql;
```
This automatically drops/creates the `apex_trade` database, creates all 5 tables (`users`, `stocks`, `orders`, `trades`, `portfolios`), and seeds 8 companies plus 2 pre-funded traders (`alice` and `bob`).

3. If your MySQL credentials are not the defaults, copy
   `db.properties.example` to `db.properties` and update the values there.
   The application also accepts `DB_URL`, `DB_USER`, and `DB_PASSWORD`
   environment variables when a corresponding property is not configured.

---

### 1.3 Step 2: JDBC Driver Placement
Place your MySQL Connector JAR inside the `lib/` directory:
```
ApexTrade/
   └── lib/
        └── mysql-connector-j-9.1.0.jar
```
*(You can download the official connector from https://dev.mysql.com/downloads/connector/j/)*

---

### 1.4 Step 3: Compilation (javac)

Open PowerShell or Command Prompt in the `ApexTrade` project root folder:

```powershell
# Windows PowerShell
mkdir bin -Force
javac -cp "lib/*" -d bin (Get-ChildItem -Path src -Recurse -Filter "*.java").FullName
```

Or on standard Command Prompt:
```cmd
mkdir bin
dir /s /b src\*.java > sources.txt
javac -cp "lib/*" -d bin @sources.txt
del sources.txt
```

---

### 1.5 Step 4: Execution (java)

Run the application:

```powershell
# Windows PowerShell / CMD
java -cp "bin;lib/*" app.ApexTrade
```

---

## 2. Sample End-to-End Test Run

### Pre-seeded Credentials for Testing
- **User 1**: `alice` / `alice123` (Starting cash: ₹500,000.00; Holds: 200 APEX, 100 ZENT, 500 SKYP)
- **User 2**: `bob` / `bob123` (Starting cash: ₹500,000.00; Holds: 300 NOVA, 150 BOLT, 100 NXGN)

---

### Test Case Walkthrough

#### Scenario A: Login and Place Matching Orders
1. **Launch**: Application displays welcome banner, connects to MySQL, initializes order books and background threads.
2. **Trader Alice places a SELL order**:
   - Menu Option: `2` (Login) -> `alice` / `alice123`
   - Dashboard Option: `4` (Trade & Order Desk) -> `2` (Place Limit SELL Order)
   - Stock Symbol: `APEX`
   - Limit Sell Price: `150.00`
   - Quantity: `50`
   - *Result*: 50 shares of APEX reserved from Alice's portfolio and placed into the APEX sell order book.
   - Select `4` (Back to Dashboard) -> Select `6` (Logout).

3. **Trader Bob places a matching BUY order**:
   - Menu Option: `2` (Login) -> `bob` / `bob123`
   - Dashboard Option: `4` (Trade & Order Desk) -> `1` (Place Limit BUY Order)
   - Stock Symbol: `APEX`
   - Limit Buy Price: `155.00`
   - Quantity: `50`
   - *Result*: ₹7,750.00 cash reserved.
   - *Automatic Matching Triggered*:
     - Buy price (₹155.00) >= Sell price (₹150.00).
     - Trade executes immediately at ₹150.00 for 50 shares!
     - Bob receives ₹250.00 refund (difference of ₹5.00/share).
     - Alice receives ₹7,500.00 credited to her cash balance.
     - Bob's portfolio now reflects 50 shares of APEX @ ₹150.00.
     - Trade logged to console, database, `data/trade_log.txt`, and `data/trades.ser`.

---

## 3. Five (5) Required Invalid Input Test Cases

| Case # | Scenario Tested | Console Inputs | Expected System Behavior | Custom Exception / Logic Used |
|---|---|---|---|---|
| **Case 1** | Duplicate User Registration | `1` (Register) -> Username: `alice` | Rejects with message: `[!] Registration Failed: Username 'alice' is already taken.` | `DuplicateUserException` caught |
| **Case 2** | Short Selling / Selling Unowned Shares | Login `bob` -> Sell `ZENT`, Price `200`, Qty `10` | Rejects with: `[!] Order Rejected: Insufficient shares! Required to sell: 10, Available: 0` | `InsufficientSharesException` caught |
| **Case 3** | Insufficient Cash Overdraft | Login `bob` -> Buy `NXGN`, Price `350`, Qty `2000` (Costs ₹700,000; Balance ₹500,000) | Rejects with: `[!] Order Rejected: Insufficient funds! Required: 700000.00, Available: 500000.00` | `InsufficientFundsException` caught |
| **Case 4** | Non-existent Stock Symbol | Login `alice` -> Buy Symbol `FAKECO`, Price `100`, Qty `10` | Rejects with: `[!] Invalid or unknown stock symbol.` | `InputValidator.isValidStockSymbol()` + `HashSet` lookup |
| **Case 5** | Negative Price / Invalid Numeric Format | Login `alice` -> Buy `APEX`, Price `-50.0` or `abc` | Rejects with: `[!] Please enter a valid decimal number.` / `Limit buy price must be positive.` | `InvalidOrderException` + `NumberFormatException` handling |
