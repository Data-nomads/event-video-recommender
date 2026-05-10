package org.example.businessunit.datamart;

import org.example.businessunit.model.Event;
import org.example.businessunit.model.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecommenderDatamart {
    private final List<Event> events;
    private final List<Video> videos;

    public RecommenderDatamart() {
        this.events = new ArrayList<>();
        this.videos = new ArrayList<>();
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void addVideo(Video video) {
        this.videos.add(video);
    }

    public List<String> getAvailableArtists() {
        Set<String> artists = events.stream()
                .map(Event::getName)
                .collect(Collectors.toSet());
        return new ArrayList<>(artists);
    }

    public List<Event> getEventsByArtist(String artistName) {
        return events.stream()
                .filter(e -> e.getName().equalsIgnoreCase(artistName))
                .collect(Collectors.toList());
    }
    public List<Video> getVideosByArtist(String artistName) {
        return videos.stream()
                .filter(v -> v.getTitle().toLowerCase().contains(artistName.toLowerCase()) ||
                        v.getChannelTitle().toLowerCase().contains(artistName.toLowerCase()))
                .limit(5)
                .collect(Collectors.toList());
    }
}
