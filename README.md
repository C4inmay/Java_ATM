# Java ATM Simulation System

A modular, object-oriented ATM simulation system built in Java with a CLI interface.

## Features

- Secure login using account number and PIN
- Maximum 3 login attempts per session
- Account lock after 3 incorrect PIN attempts
- Balance inquiry
- Deposit and withdrawal with validation
- Full transaction history
- Mini statement (last 5 transactions)
- Safe logout and exit flow
- File-based persistence for accounts and transactions

## Project Structure

- `com.atm.model`: Domain models (`Account`, `Transaction`, `TransactionType`)
- `com.atm.repository`: Data access abstraction and file-backed implementation
- `com.atm.service`: Business logic for authentication and transactions
- `com.atm.ui`: CLI presentation layer
- `com.atm.Main`: Application entry point

This separation allows the business layer to be reused in future GUI or web frontends.

## Run Instructions

From the project root:

```bash
cd "/Users/chinmay/Projects/Java Project ATM"
mkdir -p out
javac -d out $(find src/main/java -name "*.java")
java -cp out com.atm.Main
```

If you run commands from any other folder, the source path `src/main/java` will not be found.

## Data Persistence

After each successful account update, data is saved to:

- `data/accounts.csv`
- `data/transactions.csv`

These files are loaded automatically on the next run, so balances and transaction history persist across executions.

### Editing Accounts Manually

You can edit `data/accounts.csv` to change account details (for example PIN or balance).

File format:

`accountNumber,pin,balance,failedLoginAttempts,locked`

Example row:

`1001001,1234,1625.75,0,false`

When editing, keep the header and comma-separated format unchanged.

## Demo Accounts

- Account: `1001001`, PIN: `1234`
- Account: `1001002`, PIN: `4321`
- Account: `1001003`, PIN: `1111`
