package org.ilmi.service;

import org.ilmi.database.TaskDAO;
import org.ilmi.model.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TaskService {
    private final TaskDAO taskDAO = new TaskDAO();

    public Optional<Task> findById(int id) throws SQLException {
        return taskDAO.findById(id);
    }

    public List<Task> findByUserId(int userId) throws SQLException {
        return taskDAO.findByUserId(userId);
    }

    public List<Task> findAll() throws SQLException {
        return taskDAO.findAll();
    }

    public Task create(Task t) throws SQLException {
        if (t == null) throw new IllegalArgumentException("task required");
        if (t.getTitle() == null || t.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("title required");
        }
        // default status if null
        if (t.getStatus() == null) t.setStatus(Task.Status.PENDING);
        return taskDAO.insert(t);
    }

    public boolean update(Task t) throws SQLException {
        if (t == null || t.getId() == null) throw new IllegalArgumentException("task id required");
        return taskDAO.update(t);
    }

    public boolean delete(int id) throws SQLException {
        return taskDAO.delete(id);
    }
}

