package org.example.businessunit.view;

import org.example.businessunit.datamart.RecommenderDatamart;
import org.example.businessunit.model.Event;
import org.example.businessunit.model.Video;

import java.util.List;
import java.util.Scanner;

public class ConsoleDashboard {
    private final RecommenderDatamart datamart;
    private final Scanner scanner;

    public ConsoleDashboard(RecommenderDatamart datamart) {
        this.datamart = datamart;
        this.scanner = new Scanner(System.in);
    }
    public void start() {
        System.out.println("--- RECOMENDACIÓN DE VÍDEOS SEGÚN CONCIERTOS ---");

        while (true) {
            List<String> artists = datamart.getAvailableArtists();

            if (artists.isEmpty()) {
                System.out.println("Cargando artistas...");
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                continue;
            }

            System.out.println("\nArtistas disponibles con conciertos: ");
            for (int i=0; i<artists.size(); i++) {
                System.out.println((i+1) + ". " + artists.get(i));
            }
            System.out.println("0.Salir");
            System.out.println("\nElige un artista: ");

            int choice = scanner.nextInt();
            if (choice == 0) break;
            if (choice < 1 || choice > artists.size()) continue;

            String selectedArtist = artists.get(choice - 1);
            showArtistDetails(selectedArtist);
        }
    }

    private void showArtistDetails(String artist) {
        List<Event> artistEvents = datamart.getEventsByArtist(artist);
        List<Video> recommendedVideos = datamart.getVideosByArtist(artist);

        System.out.println("\n-------------------------------------------");
        System.out.println("Artista: " + artist);

        System.out.println("\nPróximos eventos:");
        if (artistEvents.isEmpty()) {
            System.out.println("No hay eventos próximos registrados.");
        } else {
            for (int i = 0; i < artistEvents.size(); i++) {
                Event e = artistEvents.get(i);
                System.out.println((i + 1) + ". " + e.getName() + " - " + e.getDate() + " - " + e.getVenue());
            }
        }

        System.out.println("\nVídeos recomendados:");
        if (recommendedVideos.isEmpty()) {
            System.out.println("Buscando vídeos en YouTube...");
        } else {
            for (int i = 0; i < recommendedVideos.size(); i++) {
                Video v = recommendedVideos.get(i);
                System.out.println((i + 1) + ". " + v.getTitle());
            }
        }
        System.out.println("-------------------------------------------");
    }
}
