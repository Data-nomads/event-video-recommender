package org.example.youtubefeeder;

import org.example.youtubefeeder.broker.YoutubePublisher;
import org.example.youtubefeeder.feeders.YoutubeFeeder;
import org.example.youtubefeeder.feeders.YoutubeVideosProvider;
import org.example.youtubefeeder.stores.ActiveMqYoutubeStore;
import org.example.youtubefeeder.stores.YoutubeStore;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Properties props = loadProperties();

        YoutubeFeeder feeder = new YoutubeVideosProvider(props);
        YoutubePublisher publisher = new YoutubePublisher();
        YoutubeStore store = new ActiveMqYoutubeStore(publisher);

        ControllerYoutube controller = new ControllerYoutube(feeder, store);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Ajustado a 15 minutos para proteger la cuota de la API de YouTube
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("--- Iniciando ciclo de captura de YouTube ---");
            controller.run();
        }, 0, 15, TimeUnit.MINUTES);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró application.properties");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error cargando application.properties", e);
        }

        return props;
    }
}