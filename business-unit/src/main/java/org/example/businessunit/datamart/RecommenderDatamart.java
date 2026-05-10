package org.example.businessunit.datamart;

import org.example.businessunit.model.Event;
import org.example.businessunit.model.Video;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        Set<String> artists = events.stream()

                .map(Event::getName)

                // Ignorar festivales y VIP
                .filter(name ->
                        !name.toLowerCase().contains("festival") &&
                                !name.toLowerCase().contains("vip"))

                .collect(Collectors.toSet());

        return new ArrayList<>(artists);
    }

    public List<Event> getEventsByArtist(String artistName) {
        return events.stream()
                .filter(e -> e.getName().equalsIgnoreCase(artistName))
                .collect(Collectors.toList());
    }

    public List<Video> getVideosByArtist(String artistName) {

        String searchName = artistName;

        // Limpiar nombres de tour
        if (artistName.contains(":")) {
            searchName = artistName.split(":")[0].trim();
        }

        if (artistName.contains(" - ")) {
            searchName = artistName.split(" - ")[0].trim();
        }

        String finalSearchName = searchName.toLowerCase();

        return videos.stream()
                .filter(v ->
                        v.getTitle().toLowerCase().contains(finalSearchName) ||
                                v.getChannelTitle().toLowerCase().contains(finalSearchName))
                .limit(5)
                .collect(Collectors.toList());
    }
}