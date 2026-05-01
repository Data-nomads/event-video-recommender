package org.example.youtubefeeder.stores;

import org.example.youtubefeeder.model.Video;

import java.util.List;

public interface YoutubeStore {
    void store(List<Video> videos);
}