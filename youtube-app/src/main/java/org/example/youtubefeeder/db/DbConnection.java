package org.example.youtubefeeder.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {

    private final Properties properties;

    public DbConnection(Properties properties) {
        this.properties = properties;
    }

    public Connection connect() throws SQLException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        String dbUrl = properties.getProperty("db.url");

        return DriverManager.getConnection(dbUrl);
    }
}