package org.example.ticketmasterfeeder.stores;

import org.example.ticketmasterfeeder.model.Event;
import java.util.List;

public interface TicketmasterStore {
    void store(List<Event> events);
}