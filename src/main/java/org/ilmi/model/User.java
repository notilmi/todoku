package org.ilmi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String email;

    private String password;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    /**
     * Map a JDBC ResultSet (snake_case columns) into a User instance.
     * Expected columns: id, username, email, password, created_at, updated_at
     */
    public static User fromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        User u = new User();
        int id = rs.getInt("id");
        if (!rs.wasNull()) u.setId(id);

        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));

        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) u.setCreatedAt(createdTs.toLocalDateTime().toLocalDate());

        java.sql.Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) u.setUpdatedAt(updatedTs.toLocalDateTime().toLocalDate());

        return u;
    }
}
