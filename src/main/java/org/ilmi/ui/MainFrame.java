package org.ilmi.ui;

import org.ilmi.model.Task;
import org.ilmi.model.User;
import org.ilmi.service.TaskService;
import org.ilmi.ui.TaskDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame {
    private final User currentUser;
    private final TaskService taskService = new TaskService();

    private JTable table;
    private DefaultTableModel tableModel;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Todoku - Tasks (" + user.getUsername() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        initComponents();
        loadTasks();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(8, 8));

        // top toolbar
        JToolBar toolbar = new JToolBar();
        JButton addBtn = new JButton("Create Task");
        JButton editBtn = new JButton("Edit Task");
        JButton deleteBtn = new JButton("Delete Task");
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);

        // table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Description", "Status", "Due Date", "Created At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);

        root.add(toolbar, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);

        setContentPane(root);

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreateTask();
            }
        });

        editBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditTask();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteTask();
            }
        });
    }

    private void loadTasks() {
        try {
            List<Task> tasks = taskService.findByUserId(currentUser.getId());
            tableModel.setRowCount(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Task t : tasks) {
                tableModel.addRow(new Object[]{
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getStatus() == null ? "" : t.getStatus().name(),
                        t.getDueDate() == null ? "" : t.getDueDate().toString(),
                        t.getCreatedAt() == null ? "" : t.getCreatedAt().format(dtf)
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onCreateTask() {
        TaskDialog dialog = new TaskDialog(this, "Create Task", null, currentUser.getId());
        dialog.setVisible(true);
        if (dialog.isSaved()) loadTasks();
    }

    private void onEditTask() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task to edit", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        try {
            Task t = taskService.findById(id).orElse(null);
            if (t == null) {
                JOptionPane.showMessageDialog(this, "Selected task not found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            TaskDialog dialog = new TaskDialog(this, "Edit Task", t, currentUser.getId());
            dialog.setVisible(true);
            if (dialog.isSaved()) loadTasks();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onDeleteTask() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer id = (Integer) tableModel.getValueAt(row, 0);
        int picked = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this task?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (picked != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = taskService.delete(id);
            if (ok) loadTasks();
            else JOptionPane.showMessageDialog(this, "Delete failed", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Throwable t) {
        JOptionPane.showMessageDialog(this, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
