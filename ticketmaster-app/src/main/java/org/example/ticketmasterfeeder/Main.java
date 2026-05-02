package org.example.ticketmasterfeeder;

import org.example.ticketmasterfeeder.broker.TicketmasterPublisher;
import org.example.ticketmasterfeeder.feeders.TicketmasterEventsProvider;
import org.example.ticketmasterfeeder.stores.ActiveMqTicketmasterStore;
import org.example.ticketmasterfeeder.stores.TicketmasterStore;

public class Main {
    public static void main(String[] args) {
        TicketmasterEventsProvider provider = new TicketmasterEventsProvider();

        TicketmasterPublisher publisher = new TicketmasterPublisher();
        TicketmasterStore store = new ActiveMqTicketmasterStore(publisher);

        ControllerTicketmaster controller = new ControllerTicketmaster(provider, store);
        controller.start();
    }
}