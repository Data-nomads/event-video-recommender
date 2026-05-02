package org.example.youtubefeeder;

import org.example.youtubefeeder.feeders.YoutubeFeeder;
import org.example.youtubefeeder.model.Video;
import org.example.youtubefeeder.stores.YoutubeStore;

import java.util.List;

public class ControllerYoutube {

    private final YoutubeFeeder feeder;
    private final YoutubeStore store;

    public ControllerYoutube(YoutubeFeeder feeder, YoutubeStore store) {
        this.feeder = feeder;
        this.store = store;
    }

    public void run() {
        List<Video> videos = feeder.feed();
        store.store(videos);

        System.out.println("Processed " + videos.size() + " YouTube videos");
    }
}