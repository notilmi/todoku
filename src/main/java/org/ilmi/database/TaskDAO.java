package org.ilmi.database;

import org.ilmi.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDAO {

    public Optional<Task> findById(int id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection c = TodokuDatabase.getInstance();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Task.fromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Task> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE user_id = ?";
        List<Task> list = new ArrayList<>();
        try (Connection c = TodokuDatabase.getInstance();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(Task.fromResultSet(rs));
                }
            }
        }
        return list;
    }

    public List<Task> findAll() throws SQLException {
        String sql = "SELECT * FROM tasks";
        List<Task> list = new ArrayList<>();
        try (Connection c = TodokuDatabase.getInstance();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(Task.fromResultSet(rs));
            }
        }
        return list;
    }

    public Task insert(Task t) throws SQLException {
        String sql = "INSERT INTO tasks (user_id, title, description, status, due_date) VALUES (?,?,?,?,?)";
        try (Connection c = TodokuDatabase.getInstance();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (t.getUserId() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, t.getUserId());
            ps.setString(2, t.getTitle());
            ps.setString(3, t.getDescription());
            ps.setString(4, t.getStatus() == null ? "pending" : t.getStatus().name().toLowerCase());
            if (t.getDueDate() == null) ps.setNull(5, Types.DATE); else ps.setDate(5, Date.valueOf(t.getDueDate()));

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setId(keys.getInt(1));
                }
            }
        }
        return t;
    }

    public boolean update(Task t) throws SQLException {
        if (t.getId() == null) throw new IllegalArgumentException("Task id required for update");
        String sql = "UPDATE tasks SET user_id=?, title=?, description=?, status=?, due_date=? WHERE id=?";
        try (Connection c = TodokuDatabase.getInstance();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (t.getUserId() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, t.getUserId());
            ps.setString(2, t.getTitle());
            ps.setString(3, t.getDescription());
            ps.setString(4, t.getStatus() == null ? "pending" : t.getStatus().name().toLowerCase());
            if (t.getDueDate() == null) ps.setNull(5, Types.DATE); else ps.setDate(5, Date.valueOf(t.getDueDate()));
            ps.setInt(6, t.getId());

            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection c = TodokuDatabase.getInstance();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

