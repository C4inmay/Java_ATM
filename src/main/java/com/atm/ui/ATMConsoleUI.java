package com.atm.ui;

import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.service.ATMOperationException;
import com.atm.service.ATMService;
import com.atm.service.AuthenticationException;
import com.atm.service.AuthenticationService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ATMConsoleUI {
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AuthenticationService authenticationService;
    private final ATMService atmService;
    private final Scanner scanner;
    private final NumberFormat currencyFormat;

    public ATMConsoleUI(AuthenticationService authenticationService, ATMService atmService) {
        this.authenticationService = authenticationService;
        this.atmService = atmService;
        this.scanner = new Scanner(System.in);
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    public void start() {
        boolean running = true;

        System.out.println("============================================");
        System.out.println("      Welcome to the ATM Simulation         ");
        System.out.println("============================================");

        while (running) {
            Account account = authenticateUser();
            if (account == null) {
                running = false;
            } else {
                running = handleSession(account);
            }
        }

        System.out.println("Session ended. Thank you for using the ATM.");
        scanner.close();
    }

    private Account authenticateUser() {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("Enter Account Number: ");
            String accountNumber = scanner.nextLine().trim();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            try {
                Account account = authenticationService.authenticate(accountNumber, pin);
                System.out.println("Login successful.\n");
                return account;
            } catch (AuthenticationException e) {
                System.out.println(e.getMessage());
                int remaining = MAX_LOGIN_ATTEMPTS - attempt;
                if (remaining > 0) {
                    System.out.println("Remaining login attempts for this session: " + remaining + "\n");
                }
            }
        }

        System.out.println("Maximum login attempts reached. Exiting system.");
        return null;
    }

    private boolean handleSession(Account account) {
        boolean loggedIn = true;

        while (loggedIn) {
            printMenu(account.getAccountNumber());
            int choice = readMenuChoice();

            switch (choice) {
                case 1:
                    showBalance(account);
                    break;
                case 2:
                    handleDeposit(account);
                    break;
                case 3:
                    handleWithdraw(account);
                    break;
                case 4:
                    showTransactionHistory(account, false);
                    break;
                case 5:
                    showTransactionHistory(account, true);
                    break;
                case 6:
                    System.out.println("Logged out safely.\n");
                    loggedIn = false;
                    break;
                case 7:
                    System.out.println("Exiting ATM system safely.");
                    return false;
                default:
                    System.out.println("Invalid option. Please choose a valid menu option.");
            }
        }

        return true;
    }

    private void printMenu(String accountNumber) {
        System.out.println("--------------------------------------------");
        System.out.println("Logged in as account: " + accountNumber);
        System.out.println("1. Balance Inquiry");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transaction History");
        System.out.println("5. Mini Statement (Last 5)");
        System.out.println("6. Logout");
        System.out.println("7. Exit System");
        System.out.println("--------------------------------------------");
        System.out.print("Select an option: ");
    }

    private int readMenuChoice() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void showBalance(Account account) {
        BigDecimal balance = atmService.getBalance(account);
        System.out.println("Current Balance: " + currencyFormat.format(balance));
    }

    private void handleDeposit(Account account) {
        BigDecimal amount = readAmount("Enter deposit amount: ");
        if (amount == null) {
            return;
        }

        try {
            atmService.deposit(account, amount);
            System.out.println("Deposit successful. New Balance: " + currencyFormat.format(atmService.getBalance(account)));
        } catch (ATMOperationException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private void handleWithdraw(Account account) {
        BigDecimal amount = readAmount("Enter withdrawal amount: ");
        if (amount == null) {
            return;
        }

        try {
            atmService.withdraw(account, amount);
            System.out.println("Withdrawal successful. New Balance: " + currencyFormat.format(atmService.getBalance(account)));
        } catch (ATMOperationException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private BigDecimal readAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        try {
            BigDecimal amount = new BigDecimal(input);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Amount must be greater than zero.");
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
            return null;
        }
    }

    private void showTransactionHistory(Account account, boolean miniStatement) {
        List<Transaction> transactions = miniStatement
                ? atmService.getRecentTransactions(account, 5)
                : atmService.getTransactionHistory(account);

        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        System.out.println("========== Transaction History ==========");
        for (Transaction transaction : transactions) {
            System.out.println(
                    transaction.getTimestamp().format(DATE_TIME_FORMATTER)
                            + " | "
                            + transaction.getType()
                            + " | "
                            + currencyFormat.format(transaction.getAmount())
            );
        }
        System.out.println("=========================================");
    }
}
