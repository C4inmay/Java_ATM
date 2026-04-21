package com.atm.ui;

import com.atm.model.Account;
import com.atm.repository.DBAccountRepository;
import com.atm.service.ATMService;
import com.atm.service.AuthenticationException;
import com.atm.service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ATMGUI {

    private static final Color BG = new Color(15, 23, 42);
    private static final Color BTN = new Color(59, 130, 246);

    public static void main(String[] args) {

        // 🔥 CONNECT TO DATABASE
        DBAccountRepository repo = new DBAccountRepository();

        AuthenticationService authService = new AuthenticationService(repo, 3);
        ATMService atmService = new ATMService(repo);

        JFrame frame = new JFrame("ATM Machine");
        frame.setSize(420, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        JLabel title = new JLabel("ATM MACHINE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 🏦 Account Label
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel accLabel = new JLabel("Account Number:");
        accLabel.setForeground(Color.WHITE);
        form.add(accLabel, gbc);

        // 🏦 Account Field
        gbc.gridx = 1;
        JTextField accountField = new JTextField(15);
        styleField(accountField);
        form.add(accountField, gbc);

        // 🔐 PIN Label
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setForeground(Color.WHITE);
        form.add(pinLabel, gbc);

        // 🔐 PIN Field
        gbc.gridx = 1;
        JPasswordField pinField = new JPasswordField(15);
        styleField(pinField);
        form.add(pinField, gbc);

        // 🔘 Login Button
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn);
        form.add(loginBtn, gbc);

        panel.add(form, BorderLayout.CENTER);

        // 🚀 LOGIN LOGIC
        loginBtn.addActionListener(e -> {

            String acc = accountField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();

            if (acc.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter Account Number and PIN ⚠");
                return;
            }

            try {
                Account account = authService.authenticate(acc, pin);

                JOptionPane.showMessageDialog(frame, "Login Successful 🎉");

                frame.dispose();

                // 👉 OPEN DASHBOARD
                new DashboardUI(account, atmService, authService);

            } catch (AuthenticationException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    // 🎨 Styling
    private static void styleField(JTextField field) {
        field.setPreferredSize(new Dimension(500, 45));
        field.setBorder(new LineBorder(new Color(59, 130, 246), 2, true));
    }

    private static void styleButton(JButton btn) {
        btn.setBackground(BTN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(500, 42));
    }
}