package org.example.ticketmasterfeeder;

import org.example.ticketmasterfeeder.adapters.TicketmasterEventsProvider;
import org.example.ticketmasterfeeder.db.EventsTicketmasterStore;
import org.example.ticketmasterfeeder.model.Event;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControllerTicketmaster {

    private final TicketmasterEventsProvider provider;
    private final EventsTicketmasterStore store;
    private final ScheduledExecutorService scheduler;

    public ControllerTicketmaster() {
        this.provider = new TicketmasterEventsProvider();
        this.store = new EventsTicketmasterStore();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        System.out.println("Iniciando...");
        Runnable tareaDeCaptura = () -> {
            System.out.println("Capturando datos (" + java.time.LocalDateTime.now() + ")...");
            try {
                List<Event> eventosNuevos = provider.getEvents();
                if (eventosNuevos.isEmpty()) {
                    System.out.println("No hay nuevos eventos");
                } else {
                    for (Event evento : eventosNuevos) {
                        store.save(evento);
                    }
                    System.out.println("Se han procesado: " + eventosNuevos.size() + " eventos.");
                }
            } catch (Exception e) {
                System.err.println("Error " + e.getMessage());
            }
        };
        scheduler.scheduleAtFixedRate(tareaDeCaptura, 0, 15, TimeUnit.SECONDS);
    }
}