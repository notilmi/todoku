package org.ilmi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private Integer id;
    private Integer userId;
    private String title;
    private String description;
    private Status status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING,
        IN_PROGRESS,
        DONE,
        CANCELLED;

        public static Status fromString(String s) {
            if (s == null) return PENDING;
            // avoid regex escaping issues by normalizing spaces and hyphens explicitly
            String normalized = s.trim().replace(' ', '_').replace('-', '_').toUpperCase();
            try {
                return Status.valueOf(normalized);
            } catch (IllegalArgumentException e) {
                // fallback to PENDING if unknown
                return PENDING;
            }
        }
    }

    /**
     * Create a Task instance from a JDBC ResultSet. This maps snake_case SQL columns to camelCase fields.
     * Expected columns: id, user_id, title, description, status, due_date, created_at, updated_at
     */
    public static Task fromResultSet(ResultSet rs) throws SQLException {
        Task t = new Task();
        // basic columns
        int id = rs.getInt("id");
        if (!rs.wasNull()) t.setId(id);

        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) t.setUserId(userId);

        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));

        t.setStatus(Status.fromString(rs.getString("status")));

        java.sql.Date due = rs.getDate("due_date");
        if (due != null) t.setDueDate(due.toLocalDate());

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) t.setCreatedAt(createdTs.toLocalDateTime());

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) t.setUpdatedAt(updatedTs.toLocalDateTime());

        return t;
    }
}
