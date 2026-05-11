package org.example.businessunit.view;

import org.example.businessunit.datamart.RecommenderDatamart;
import org.example.businessunit.model.Event;
import org.example.businessunit.service.YoutubeSearchService;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ConsoleDashboard {

    private static final int MAX_ARTISTS_TO_SHOW = 20;
    private static final int MAX_EVENTS_TO_SHOW = 5;

    private final RecommenderDatamart datamart;
    private final Scanner scanner;
    private final YoutubeSearchService youtubeSearchService;

    public ConsoleDashboard(RecommenderDatamart datamart) {
        this.datamart = datamart;
        this.scanner = new Scanner(System.in);
        this.youtubeSearchService = new YoutubeSearchService();
    }

    public void start() {

        printHeader();

        while (true) {

            List<String> artists = datamart.getAvailableArtists()
                    .stream()
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();

            if (artists.isEmpty()) {
                System.out.println("\nCargando artistas desde el datamart...");
                sleep();
                continue;
            }

            int maxToShow = Math.min(MAX_ARTISTS_TO_SHOW, artists.size());

            System.out.println("\nArtistas disponibles con conciertos:");
            System.out.println("(Mostrando " + maxToShow + " de " + artists.size()
                    + ". También puedes escribir cualquier artista manualmente)\n");

            for (int i = 0; i < maxToShow; i++) {
                System.out.println((i + 1) + ". " + artists.get(i));
            }

            System.out.println("\n0. Salir");
            System.out.print("\nElige un número o escribe cualquier artista: ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("\nCerrando sistema de recomendaciones...");
                break;
            }

            if (input.isBlank()) {
                System.out.println("\nEntrada vacía. Inténtalo de nuevo.");
                continue;
            }

            String selectedArtist = getSelectedArtist(input, artists, maxToShow);

            if (selectedArtist == null) {
                continue;
            }

            showArtistDetails(selectedArtist);
        }
    }

    private String getSelectedArtist(String input, List<String> artists, int maxToShow) {

        try {

            int choice = Integer.parseInt(input);

            if (choice < 1 || choice > maxToShow) {
                System.out.println("\nOpción no válida. Elige un número entre 1 y "
                        + maxToShow + " o escribe un artista manualmente.");
                return null;
            }

            return artists.get(choice - 1);

        } catch (NumberFormatException e) {

            return input;
        }
    }

    private void showArtistDetails(String artist) {

        List<Event> artistEvents = datamart.getEventsByArtist(artist)
                .stream()
                .sorted(Comparator.comparing(Event::getDate))
                .limit(MAX_EVENTS_TO_SHOW)
                .toList();

        List<String> recommendedVideos = youtubeSearchService.searchVideos(artist);

        System.out.println("\n=================================================");
        System.out.println("Artista seleccionado: " + artist);
        System.out.println("=================================================");

        System.out.println("\nPróximos eventos:");

        if (artistEvents.isEmpty()) {

            System.out.println("No hay conciertos disponibles para este artista en los datos actuales de Ticketmaster.");

        } else {

            System.out.println("Mostrando hasta " + MAX_EVENTS_TO_SHOW + " próximos eventos disponibles:\n");

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
                System.out.println((i + 1) + ". " + recommendedVideos.get(i));
            }
        }

        System.out.println("\n=================================================");
    }

    private void printHeader() {
        System.out.println("\n=================================================");
        System.out.println("   SISTEMA DE RECOMENDACIÓN DE VÍDEOS Y EVENTOS");
        System.out.println("=================================================");
        System.out.println("Consulta conciertos disponibles y vídeos populares relacionados.");
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}