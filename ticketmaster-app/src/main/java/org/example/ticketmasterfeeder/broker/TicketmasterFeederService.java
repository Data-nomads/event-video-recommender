package org.example.ticketmasterfeeder;

import org.example.ticketmasterfeeder.adapters.TicketmasterEventsProvider;
import org.example.ticketmasterfeeder.broker.TicketmasterPublisher;
import org.example.ticketmasterfeeder.model.Event;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TicketmasterFeederService {

    private final TicketmasterEventsProvider provider;
    private final TicketmasterPublisher publisher;
    private final ScheduledExecutorService scheduler;

    public TicketmasterFeederService() {
        this.provider = new TicketmasterEventsProvider();
        this.publisher = new TicketmasterPublisher();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        System.out.println("Iniciando Ticketmaster Feeder. Conectando a ActiveMQ...");

        Runnable tareaDeCaptura = () -> {
            try {
                List<Event> eventosNuevos = provider.getEvents();

                if (!eventosNuevos.isEmpty()) {
                    for (Event evento : eventosNuevos) {
                        publisher.publish(evento);
                    }
                    System.out.println("Captura y publicacion completada.");
                }
            } catch (Exception e) {
                System.err.println("Error durante la ejecucion: " + e.getMessage());
            }
        };

        scheduler.scheduleAtFixedRate(tareaDeCaptura, 0, 6, TimeUnit.HOURS);
    }
}