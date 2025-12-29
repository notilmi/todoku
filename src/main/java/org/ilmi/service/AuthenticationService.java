package org.ilmi.service;

import org.ilmi.database.UserDAO;
import org.ilmi.model.User;

import java.sql.SQLException;
import java.util.Optional;

public class AuthenticationService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Attempt to login with username and password. Returns Optional<User> when credentials match.
     * Note: password is compared in plain text per request (no hashing).
     */
    public Optional<User> login(String username, String password) throws SQLException {
        Optional<User> maybe = userDAO.findByUsername(username);
        if (maybe.isPresent()) {
            User u = maybe.get();
            if (u.getPassword() != null && u.getPassword().equals(password)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    /**
     * Register a new user. Throws IllegalArgumentException if required fields are missing or username/email already exists.
     */
    public User register(User u) throws SQLException {
        if (u == null) throw new IllegalArgumentException("User must not be null");
        if (u.getUsername() == null || u.getUsername().trim().isEmpty())
            throw new IllegalArgumentException("username required");
        if (u.getEmail() == null || u.getEmail().trim().isEmpty())
            throw new IllegalArgumentException("email required");
        if (u.getPassword() == null || u.getPassword().isEmpty())
            throw new IllegalArgumentException("password required");

        if (userDAO.findByUsername(u.getUsername()).isPresent()) {
            throw new IllegalArgumentException("username already exists");
        }

        // check email uniqueness (DAO doesn't have findByEmail, so use findAll)
        for (User existing : userDAO.findAll()) {
            if (existing.getEmail() != null && existing.getEmail().equalsIgnoreCase(u.getEmail())) {
                throw new IllegalArgumentException("email already exists");
            }
        }

        return userDAO.insert(u);
    }
}

