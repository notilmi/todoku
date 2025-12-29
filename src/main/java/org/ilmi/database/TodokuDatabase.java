package org.ilmi.database;

import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class TodokuDatabase {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/todoku-db";
    private static final String USER = "root";
    private static final String PASSWORD = "example-root-pw";

    private static Connection INSTANCE;

    private TodokuDatabase() {
        // Private constructor to prevent instantiation
    }

    public static Connection getInstance() throws SQLException {
        if (INSTANCE == null || INSTANCE.isClosed()) {
            INSTANCE = getConnection();
        }
        return INSTANCE;
    }

    @Nullable
    public static Connection getConnection( ) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

            return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menyambungkan ke database "+e.getMessage());
        }
        return null;
    }

}
