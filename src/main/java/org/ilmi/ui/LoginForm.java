package org.ilmi.ui;

import org.ilmi.model.User;
import org.ilmi.service.AuthenticationService;
import org.ilmi.ui.MainFrame;
import org.ilmi.ui.RegisterForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Optional;

public class LoginForm extends JFrame {
    private final AuthenticationService authService = new AuthenticationService();

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login - Todoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel fields = new JPanel(new GridLayout(2, 2, 8, 8));
        fields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        fields.add(new JLabel("Username:"));
        usernameField = new JTextField();
        fields.add(usernameField);
        fields.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        fields.add(passwordField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        buttons.add(registerBtn);
        buttons.add(loginBtn);

        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        setContentPane(panel);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpenRegister();
            }
        });
    }

    private void onLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        try {
            Optional<User> maybe = authService.login(username, password);
            if (maybe.isPresent()) {
                // open main page
                User u = maybe.get();
                SwingUtilities.invokeLater(() -> {
                    MainFrame main = new MainFrame(u);
                    main.setVisible(true);
                });
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            showError(ex);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onOpenRegister() {
        SwingUtilities.invokeLater(() -> {
            RegisterForm rf = new RegisterForm();
            rf.setVisible(true);
        });
        dispose();
    }

    private void showError(Throwable t) {
        JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
