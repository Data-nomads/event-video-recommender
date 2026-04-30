package org.example.youtubefeeder;

import org.example.youtubefeeder.adapters.YoutubeVideosProvider;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Properties props = loadProperties();

        YoutubeVideosProvider provider = new YoutubeVideosProvider(props);
        ControllerYoutube controller = new ControllerYoutube(provider);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Buscando videos en YouTube...");
            controller.run();
        }, 0, 1, TimeUnit.MINUTES);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontro application.properties");
            }
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error cargando application.properties", e);
        }

        return props;
    }
}