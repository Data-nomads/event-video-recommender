package org.example.ticketmasterfeeder.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String DB_URL = "jdbc:sqlite:ticketmaster.db";

    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return connection;
    }
}
