package org.ilmi.ui;

import org.ilmi.model.Task;
import org.ilmi.service.TaskService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;

public class TaskDialog extends JDialog {
    private final TaskService taskService = new TaskService();
    private final Integer userId;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> statusCombo;
    private JTextField dueDateField; // yyyy-MM-dd

    private JButton saveBtn;
    private JButton cancelBtn;

    private boolean saved = false;
    private Task editing;

    public TaskDialog(Frame owner, String title, Task editing, Integer userId) {
        super(owner, title, true);
        this.editing = editing;
        this.userId = userId;
        setSize(500, 400);
        setLocationRelativeTo(owner);
        initComponents();
        if (editing != null) loadEditing();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; fields.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; titleField = new JTextField(); fields.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; fields.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; descriptionArea = new JTextArea(6, 30); fields.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; fields.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; statusCombo = new JComboBox<>(new String[]{"PENDING", "IN_PROGRESS", "DONE", "CANCELLED"}); fields.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; fields.add(new JLabel("Due date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; dueDateField = new JTextField(); fields.add(dueDateField, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveBtn = new JButton("Save");
        cancelBtn = new JButton("Cancel");
        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        root.add(fields, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saved = false;
                dispose();
            }
        });
    }

    private void loadEditing() {
        titleField.setText(editing.getTitle());
        descriptionArea.setText(editing.getDescription());
        statusCombo.setSelectedItem(editing.getStatus() == null ? "PENDING" : editing.getStatus().name());
        dueDateField.setText(editing.getDueDate() == null ? "" : editing.getDueDate().toString());
    }

    private void onSave() {
        String title = titleField.getText();
        if (title == null || title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required", "Validation error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (editing == null) editing = new Task();
            editing.setUserId(userId);
            editing.setTitle(title);
            editing.setDescription(descriptionArea.getText());
            editing.setStatus(Task.Status.valueOf((String) statusCombo.getSelectedItem()));
            String due = dueDateField.getText();
            if (due == null || due.trim().isEmpty()) editing.setDueDate(null);
            else editing.setDueDate(LocalDate.parse(due));

            if (editing.getId() == null) {
                taskService.create(editing);
            } else {
                taskService.update(editing);
            }
            saved = true;
            dispose();
        } catch (SQLException ex) {
            showError(ex);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private void showError(Throwable t) {
        JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

