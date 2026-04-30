package org.example.ticketmasterfeeder.model;

import java.time.Instant;

public class Event {

    private final String ts;
    private final String ss;
    private final String id;
    private final String name;
    private final String date;
    private final String venue;

    public Event(String id, String name, String date, String venue) {
        this.ts = Instant.now().toString();
        this.ss = "ticketmaster-feeder";
        this.id = id;
        this.name = name;
        this.date = date;
        this.venue = venue;
    }

    public String getTs() { return ts; }
    public String getSs() { return ss; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getVenue() { return venue; }
}