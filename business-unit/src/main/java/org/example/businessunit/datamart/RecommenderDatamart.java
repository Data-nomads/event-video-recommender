package org.example.businessunit.datamart;

import org.example.businessunit.model.Event;
import org.example.businessunit.model.Video;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RecommenderDatamart {

    private final List<Event> events;
    private final List<Video> videos;

    private final Set<String> eventIds;
    private final Set<String> videoIds;

    public RecommenderDatamart() {

        this.events = new ArrayList<>();
        this.videos = new ArrayList<>();

        this.eventIds = new HashSet<>();
        this.videoIds = new HashSet<>();
    }

    public void addEvent(Event event) {

        if (!eventIds.contains(event.getId())) {

            this.events.add(event);
            this.eventIds.add(event.getId());
        }
    }

    public void addVideo(Video video) {

        if (!videoIds.contains(video.getVideoId())) {

            this.videos.add(video);
            this.videoIds.add(video.getVideoId());
        }
    }

    public List<String> getAvailableArtists() {

        List<Event> eventsSnapshot = new ArrayList<>(events);

        Set<String> artists = eventsSnapshot.stream()
                .map(Event::getName)
                .map(this::normalizeArtistName)
                .filter(this::isValidArtistOrTour)
                .collect(Collectors.toCollection(TreeSet::new));

        return new ArrayList<>(artists);
    }

    private String normalizeArtistName(String name) {

        if (name == null) {
            return "";
        }

        String normalized = name;

        // Eliminar VIP
        normalized = normalized.replace("| VIP Packages", "");

        // Si contiene " - ", quedarse con la primera parte
        if (normalized.contains(" - ")) {
            normalized = normalized.split(" - ")[0];
        }

        return normalized.trim();
    }

    private boolean isValidArtistOrTour(String name) {

        if (name == null || name.isBlank()) {
            return false;
        }

        String lowerName = name.toLowerCase();

        return !lowerName.contains("festival")
                && !lowerName.contains("vip")
                && !lowerName.contains("package")
                && !lowerName.contains("parking")
                && !lowerName.contains("plaza de parking")
                && !lowerName.contains("1001 músicas")
                && !lowerName.contains("caixabank");
    }

    public List<Event> getEventsByArtist(String artistName) {

        List<Event> eventsSnapshot = new ArrayList<>(events);

        String search = artistName.toLowerCase();

        return eventsSnapshot.stream()
                .filter(e ->
                        normalizeArtistName(e.getName())
                                .toLowerCase()
                                .contains(search))
                .collect(Collectors.toList());
    }

    public List<Video> getVideosByArtist(String artistName) {

        String searchName = artistName;

        if (artistName.contains(":")) {
            searchName = artistName.split(":")[0].trim();
        }

        if (artistName.contains(" - ")) {
            searchName = artistName.split(" - ")[0].trim();
        }

        String finalSearchName = searchName.toLowerCase();

        return videos.stream()
                .filter(v ->
                        v.getTitle().toLowerCase().contains(finalSearchName)
                                || v.getChannelTitle().toLowerCase().contains(finalSearchName))
                .limit(5)
                .collect(Collectors.toList());
    }
}b