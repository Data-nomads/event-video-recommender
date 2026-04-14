package org.example.ticketmasterfeeder.db;

import org.example.ticketmasterfeeder.model.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class EventsTicketmasterStore {

    public EventsTicketmasterStore() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS events ("
                + "internal_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ticketmaster_id TEXT NOT NULL, "
                + "name TEXT NOT NULL, "
                + "event_date TEXT, "
                + "venue TEXT, "
                + "captured_at TEXT NOT NULL"
                + ");";

        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Tabla creada");

        } catch (SQLException e) {
            System.err.println("Error creando tabla " + e.getMessage());
        }
    }

    public void save(Event event) {
        String sql = "INSERT INTO events (ticketmaster_id, name, event_date, venue, captured_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getId());
            pstmt.setString(2, event.getName());
            pstmt.setString(3, event.getDate());
            pstmt.setString(4, event.getVenue());
            pstmt.setString(5, event.getCapturedAt().toString());

            pstmt.executeUpdate();
            System.out.println("Eventos guardados: " + event.getName() + " a las " + event.getCapturedAt());

        } catch (SQLException e) {
            System.err.println("Error guardando evento: " + e.getMessage());
        }
    }
}