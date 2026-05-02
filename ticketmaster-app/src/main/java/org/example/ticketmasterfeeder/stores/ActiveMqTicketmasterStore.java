package org.example.ticketmasterfeeder.stores;

import org.example.ticketmasterfeeder.broker.TicketmasterPublisher;
import org.example.ticketmasterfeeder.model.Event;

import java.util.List;

public class ActiveMqTicketmasterStore implements TicketmasterStore {

    private final TicketmasterPublisher publisher;

    public ActiveMqTicketmasterStore(TicketmasterPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void store(List<Event> events) {
        for (Event event : events) {
            publisher.publish(event);
        }

        System.out.println("Published " + events.size() + " Ticketmaster events to ActiveMQ");
    }
}