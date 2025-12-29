package org.ilmi.ui;

import org.ilmi.model.User;
import org.ilmi.service.AuthenticationService;
import org.ilmi.ui.LoginForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterForm extends JFrame {
    private final AuthenticationService authService = new AuthenticationService();

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public RegisterForm() {
        setTitle("Register - Todoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 220);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel fields = new JPanel(new GridLayout(3, 2, 8, 8));
        fields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        fields.add(new JLabel("Username:"));
        usernameField = new JTextField();
        fields.add(usernameField);
        fields.add(new JLabel("Email:"));
        emailField = new JTextField();
        fields.add(emailField);
        fields.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        fields.add(passwordField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back to Login");
        buttons.add(backBtn);
        buttons.add(registerBtn);

        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        setContentPane(panel);

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRegister();
            }
        });

        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onBack();
            }
        });
    }

    private void onRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        try {
            User u = new User();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword(password);
            User created = authService.register(u);
            JOptionPane.showMessageDialog(this, "Registration successful. Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                LoginForm lf = new LoginForm();
                lf.setVisible(true);
            });
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onBack() {
        SwingUtilities.invokeLater(() -> {
            LoginForm lf = new LoginForm();
            lf.setVisible(true);
        });
        dispose();
    }

    private void showError(Throwable t) {
        JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
