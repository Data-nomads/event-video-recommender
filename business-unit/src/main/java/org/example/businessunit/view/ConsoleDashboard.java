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

        System.out.println("\n=================================================");
        System.out.println("   SISTEMA DE RECOMENDACIÓN DE VÍDEOS Y EVENTOS");
        System.out.println("=================================================");

        while (true) {

            List<String> artists = datamart.getAvailableArtists();

            if (artists.isEmpty()) {
                System.out.println("\nCargando artistas...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            System.out.println("\nArtistas disponibles con conciertos:\n");

            for (int i = 0; i < artists.size(); i++) {
                System.out.println((i + 1) + ". " + artists.get(i));
            }

            System.out.println("\n0. Salir");
            System.out.print("\nElige un artista: ");

            int choice = scanner.nextInt();

            if (choice == 0) {
                System.out.println("\nCerrando sistema de recomendaciones...");
                break;
            }

            if (choice < 1 || choice > artists.size()) {
                System.out.println("\nOpción no válida.");
                continue;
            }

            String selectedArtist = artists.get(choice - 1);

            showArtistDetails(selectedArtist);
        }
    }

    private void showArtistDetails(String artist) {

        List<Event> artistEvents = datamart.getEventsByArtist(artist);
        List<Video> recommendedVideos = datamart.getVideosByArtist(artist);

        System.out.println("\n=================================================");
        System.out.println("Artista seleccionado:");
        System.out.println(artist);
        System.out.println("=================================================");

        System.out.println("\nPróximos eventos:");

        if (artistEvents.isEmpty()) {

            System.out.println("No hay eventos próximos registrados.");

        } else {

            for (int i = 0; i < artistEvents.size(); i++) {

                Event e = artistEvents.get(i);

                System.out.println(
                        (i + 1) + ". "
                                + e.getName()
                                + " | Fecha: " + e.getDate()
                                + " | Lugar: " + e.getVenue()
                );
            }
        }

        System.out.println("\nRecomendaciones basadas en vídeos populares de YouTube:");

        if (recommendedVideos.isEmpty()) {

            System.out.println("No se encontraron vídeos relacionados.");

        } else {

            for (int i = 0; i < recommendedVideos.size(); i++) {

                Video v = recommendedVideos.get(i);

                System.out.println(
                        (i + 1) + ". "
                                + v.getTitle()
                );
            }
        }

        System.out.println("\n=================================================");
    }
}