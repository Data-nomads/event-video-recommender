package org.example.youtubefeeder;

import org.example.youtubefeeder.adapters.YoutubeVideosProvider;
import org.example.youtubefeeder.db.DbConnection;
import org.example.youtubefeeder.db.VideosYoutubeStore;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Properties props = loadProperties();

        YoutubeVideosProvider provider = new YoutubeVideosProvider(props);
        DbConnection dbConnection = new DbConnection(props);
        VideosYoutubeStore store = new VideosYoutubeStore(dbConnection);
        ControllerYoutube controller = new ControllerYoutube(provider, store);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Fetching YouTube videos...");
            controller.run();
        }, 0, 1, TimeUnit.MINUTES);
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