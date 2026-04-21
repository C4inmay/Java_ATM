package com.atm.ui;

import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.service.ATMOperationException;
import com.atm.service.ATMService;
import com.atm.service.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DashboardUI {

    private static final Color BG = new Color(15, 23, 42);
    private static final Color BTN = new Color(59, 130, 246);

    public DashboardUI(Account account, ATMService atmService, AuthenticationService authService) {

        JFrame frame = new JFrame("ATM Dashboard");
        frame.setSize(600, 600); // 🔥 increased frame size to fit 500 width properly
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel welcome = new JLabel("Welcome User " + account.getAccountNumber());
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel balanceLabel = new JLabel("Your current Balance: ₹ " + account.getBalance());
        balanceLabel.setForeground(Color.GREEN);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton balanceBtn = createButton("Check Balance");
        JButton depositBtn = createButton("Deposit");
        JButton withdrawBtn = createButton("Withdraw");
        JButton historyBtn = createButton("Transaction History");
        JButton logoutBtn = createButton("Logout");

        panel.add(welcome);
        panel.add(Box.createVerticalStrut(10));
        panel.add(balanceLabel);
        panel.add(Box.createVerticalStrut(20));

        panel.add(balanceBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(depositBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(withdrawBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(historyBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(logoutBtn);

        frame.add(panel);
        frame.setVisible(true);

        // 💰 CHECK BALANCE
        balanceBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, "Your Balance: ₹ " + account.getBalance())
        );

        // 💸 DEPOSIT
        depositBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter deposit amount:");

            if (input == null || input.trim().isEmpty()) return;

            try {
                BigDecimal amount = new BigDecimal(input);

                atmService.deposit(account, amount);
                balanceLabel.setText("Balance: ₹ " + account.getBalance());

                JOptionPane.showMessageDialog(frame, "Deposit Successful");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        // 💳 WITHDRAW
        withdrawBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter withdrawal amount:");

            if (input == null || input.trim().isEmpty()) return;

            try {
                BigDecimal amount = new BigDecimal(input);

                atmService.withdraw(account, amount);
                balanceLabel.setText("Balance: ₹ " + account.getBalance());

                JOptionPane.showMessageDialog(frame, "Withdrawal Successful");

            } catch (ATMOperationException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        // 🧾 TRANSACTION HISTORY
        historyBtn.addActionListener(e -> {
            StringBuilder history = new StringBuilder();

            List<Transaction> transactions = atmService.getTransactionHistory(account);

            if (transactions.isEmpty()) {
                history.append("No Transactions Found");
            } else {
                for (Transaction tx : transactions) {
                    history.append(tx.getType().name())
                            .append(" ₹")
                            .append(tx.getAmount())
                            .append("\n");
                }
            }

            JOptionPane.showMessageDialog(frame, history.toString());
        });

        // 🚪 LOGOUT
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new ATMGUI();
        });
    }

    // ⭐ THIS is what actually controls width in BoxLayout
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BTN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btn.setMaximumSize(new Dimension(500, 45)); // 🔥 THIS makes width = 500
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        return btn;
    }
}