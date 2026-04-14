package org.example.ticketmasterfeeder.model;

import java.time.LocalDateTime;

public class Event {

    private final String id;
    private final String name;
    private final String date;
    private final String venue;
    private final LocalDateTime capturedAt;

    public Event(String id, String name, String date, String venue, LocalDateTime capturedAt) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.capturedAt = capturedAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getVenue() {
        return venue;
    }

    public LocalDateTime getCapturedAt() {
        return capturedAt;
    }
    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", venue='" + venue + '\'' +
                ", capturedAt=" + capturedAt +
                '}';
    }
}