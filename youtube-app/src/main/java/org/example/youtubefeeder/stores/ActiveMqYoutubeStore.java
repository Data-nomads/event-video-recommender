package org.example.youtubefeeder.stores;

import org.example.youtubefeeder.broker.YoutubePublisher;
import org.example.youtubefeeder.model.Video;

import java.util.List;

public class ActiveMqYoutubeStore implements YoutubeStore {

    private final YoutubePublisher publisher;

    public ActiveMqYoutubeStore(YoutubePublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void store(List<Video> videos) {
        for (Video video : videos) {
            publisher.publish(video);
        }

        System.out.println("Published " + videos.size() + " YouTube videos to ActiveMQ");
    }
}