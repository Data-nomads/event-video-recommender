package org.example.ticketmasterfeeder.feeders;

import org.example.ticketmasterfeeder.broker.TicketmasterPublisher;
import org.example.ticketmasterfeeder.model.Event;

import java.util.List;

public class TicketmasterFeeder {

    private final TicketmasterEventsProvider provider;
    private final TicketmasterPublisher publisher;

    public TicketmasterFeeder(TicketmasterEventsProvider provider,
                              TicketmasterPublisher publisher) {
        this.provider = provider;
        this.publisher = publisher;
    }

    public void run() {
        List<Event> events = provider.getEvents();

        for (Event event : events) {
            publisher.publish(event);
        }

        System.out.println("Published " + events.size() + " events");
    }
}