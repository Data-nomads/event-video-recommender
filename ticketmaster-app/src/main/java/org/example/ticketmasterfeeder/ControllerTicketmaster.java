package org.example.ticketmasterfeeder;

import org.example.ticketmasterfeeder.feeders.TicketmasterEventsProvider;
import org.example.ticketmasterfeeder.model.Event;
import org.example.ticketmasterfeeder.stores.TicketmasterStore;
import org.example.ticketmasterfeeder.broker.TicketmasterPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControllerTicketmaster {

    private final TicketmasterEventsProvider provider;
    private final TicketmasterStore store;
    private final ScheduledExecutorService scheduler;
    private final TicketmasterPublisher publisher;

    public ControllerTicketmaster(TicketmasterEventsProvider provider, TicketmasterStore store) {
        this.provider = provider;
        this.store = store;
        this.publisher = new TicketmasterPublisher();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        System.out.println("Starting Ticketmaster feeder...");

        Runnable captureTask = () -> {
            System.out.println("Capturing Ticketmaster data (" + LocalDateTime.now() + ")...");

            try {
                List<Event> events = provider.getEvents();

                if (events.isEmpty()) {
                    System.out.println("No new Ticketmaster events");
                } else {
                    for (Event event : events) {
                        publisher.publish(event);
                    }

                    System.out.println("Processed " + events.size() + " Ticketmaster events");
                }

            } catch (Exception e) {
                System.err.println("Ticketmaster capture error: " + e.getMessage());
            }
        };

        scheduler.scheduleAtFixedRate(captureTask, 0, 15, TimeUnit.SECONDS);
    }
}