# ATM Project Detailed Guide for Beginners

## 1) What this project is
This project is a Java console ATM simulator.
It behaves like a basic ATM machine:
- User logs in with account number and PIN
- User checks balance
- User deposits money
- User withdraws money
- User sees transaction history
- User logs out or exits

Important point:
This is not only a console app. It is built in layers, so later you can replace the console screen with a GUI or web frontend without rewriting core banking logic.

## 2) Big picture architecture
The project follows layered design:

- UI layer
  Handles user input and prints output
  File: src/main/java/com/atm/ui/ATMConsoleUI.java

- Service layer
  Contains business rules (auth rules, balance rules, transaction rules)
  Files:
  src/main/java/com/atm/service/AuthenticationService.java
  src/main/java/com/atm/service/ATMService.java

- Repository layer
  Handles data storage and retrieval
  Files:
  src/main/java/com/atm/repository/AccountRepository.java
  src/main/java/com/atm/repository/FileAccountRepository.java
  src/main/java/com/atm/repository/InMemoryAccountRepository.java

- Model layer
  Core domain objects (Account, Transaction, TransactionType)
  Files:
  src/main/java/com/atm/model/Account.java
  src/main/java/com/atm/model/Transaction.java
  src/main/java/com/atm/model/TransactionType.java

- Entry point
  Boots the app and wires dependencies
  File: src/main/java/com/atm/Main.java

## 3) Think in "who does what"
Simple way to understand the connection:

- ATMConsoleUI asks user for action
- ATMConsoleUI calls Service methods
- Service applies business rules
- Service asks Repository to save or load
- Repository reads/writes CSV files
- Model classes carry account and transaction data

This separation is the main reason the project is clean and extendable.

## 4) Startup workflow (exactly what happens)
When you run the program, Main.main does this:

1. Creates FileAccountRepository with data directory path "data"
2. Repository loads data from:
   - data/accounts.csv
   - data/transactions.csv
3. If no accounts exist yet, Main seeds demo accounts
4. Main creates:
   - AuthenticationService
   - ATMService
5. Main creates ATMConsoleUI and calls start()
6. UI loop begins

So data loading happens first, then services, then user interaction.

## 5) CLI "pages" and what each one does
Even though this is console-based, you can think of it as pages/screens.

### Page A: Login screen
Shown by method authenticateUser in ATMConsoleUI.

User enters:
- account number
- PIN

UI calls AuthenticationService.authenticate(accountNumber, pin).

Possible outcomes:
- Success: returns Account object and opens menu page
- Wrong account or PIN: error message, tries again
- 3 session attempts finished: exits system
- Account locked in data: immediate lock message

### Page B: Main menu screen
Shown by printMenu and handled by handleSession.

Menu options:
1. Balance Inquiry
2. Deposit
3. Withdraw
4. Transaction History
5. Mini Statement (last 5)
6. Logout
7. Exit System

### Page C: Balance inquiry
UI calls ATMService.getBalance(account).
Service returns current balance from Account.
No storage write here (read-only action).

### Page D: Deposit flow
UI reads amount from user.
UI calls ATMService.deposit(account, amount).

ATMService.deposit does:
1. account.deposit(amount) (validation: amount > 0)
2. account.addTransaction(DEPOSIT, amount)
3. accountRepository.save(account) (persists to files)

Result:
- Balance updated
- New transaction added
- Files updated immediately

### Page E: Withdraw flow
UI reads amount from user.
UI calls ATMService.withdraw(account, amount).

ATMService.withdraw does:
1. account.withdraw(amount)
   - validation: amount > 0
   - validation: sufficient balance
2. account.addTransaction(WITHDRAW, amount)
3. accountRepository.save(account)

Result:
- Balance reduced if valid
- Withdraw transaction saved
- Files updated immediately

### Page F: Transaction history
UI calls one of:
- ATMService.getTransactionHistory(account)
- ATMService.getRecentTransactions(account, 5)

UI formats and prints timestamp, type, amount.
No storage write here.

### Page G: Logout and Exit
- Logout ends current user session, returns to login screen
- Exit ends entire application loop

## 6) Authentication and lock behavior
Authentication rules are in AuthenticationService:

- Account must exist
- Account must not be locked
- PIN must match

On successful PIN:
- failed attempts reset to 0
- account saved to repository

On wrong PIN:
- failed attempts increment
- account saved
- if attempts reach maxAttempts (3), locked=true

Lock state is stored in accounts.csv, so lock remains after restart.

## 7) Data storage files (persistent)
Repository implementation currently used: FileAccountRepository.

### accounts.csv
Path: data/accounts.csv
Columns:
- accountNumber
- pin
- balance
- failedLoginAttempts
- locked

Example:
1001001,1234,1630.75,0,false

### transactions.csv
Path: data/transactions.csv
Columns:
- accountNumber
- type
- amount
- timestamp

Example:
1001001,DEPOSIT,125.75,2026-04-19T22:46:11.370213

## 8) How repository reads and writes data
In FileAccountRepository:

- Constructor:
  - creates data folder if missing
  - loads accounts and transactions from CSV

- loadTransactions:
  - reads transactions.csv
  - groups rows by account number

- loadFromFiles:
  - reads accounts.csv
  - creates Account objects
  - attaches each account's transaction list

- save(account):
  - updates in-memory map
  - calls persistAll()

- persistAll:
  - rewrites full accounts.csv
  - rewrites full transactions.csv

So every meaningful update is persisted immediately.

## 9) Why both FileAccountRepository and InMemoryAccountRepository exist
- FileAccountRepository
  Real persistence, used by Main now

- InMemoryAccountRepository
  Useful for testing or temporary runtime-only mode
  Data vanishes when app stops

Main currently uses FileAccountRepository.

## 10) Error handling strategy
The app uses custom exceptions to keep code clean:

- AuthenticationException
  Used for login-related failures

- ATMOperationException
  Used for deposit/withdraw business errors

Flow:
- Service throws meaningful exception
- UI catches exception and prints friendly message

This keeps business code and display code separate.

## 11) How to manually edit accounts safely
If you want to change real account data manually:

1. Stop the app first
2. Open data/accounts.csv
3. Edit values carefully
4. Keep CSV format exactly same
5. Save file
6. Run app again

Tips:
- locked must be true or false
- failedLoginAttempts should be a number
- balance should be numeric (example 2500.00)
- do not remove header line

You can also edit transactions.csv, but keep timestamp format as ISO date-time.

## 12) End-to-end sequence examples

### Example 1: Successful deposit
1. User logs in
2. UI gets deposit amount
3. ATMService validates and updates account
4. ATMService records DEPOSIT transaction
5. Repository save triggers file write
6. User sees new balance

### Example 2: Wrong PIN 3 times
1. User enters wrong PIN
2. AuthenticationService increments failed attempts
3. On third wrong attempt, locked=true
4. Repository saves lock status in accounts.csv
5. Next login attempts for that account are blocked

## 13) If you want GUI or web later, what changes
Good news: core logic is already reusable.

Usually you only replace UI layer:
- Replace ATMConsoleUI with JavaFX/Swing/React/Spring controller layer
- Keep services and repository mostly same

Possible future evolution:
- Keep FileAccountRepository for local file mode
- Add DatabaseAccountRepository for MySQL/PostgreSQL
- Switch implementation with minimal code changes because of AccountRepository interface

## 14) Run steps
From project root:

cd "/Users/chinmay/Projects/Java Project ATM"
mkdir -p out
javac -d out $(find src/main/java -name "*.java")
java -cp out com.atm.Main

## 15) Quick mental model to remember
If you forget architecture, remember this line:

UI asks Services, Services enforce rules, Repository saves data, Models carry data.

That is the whole project flow.
