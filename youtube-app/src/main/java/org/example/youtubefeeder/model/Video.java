package org.example.youtubefeeder.model;

import java.time.Instant;

public class Video {
    private final String ts;
    private final String ss;
    private String videoId;
    private String title;
    private String channelTitle;
    private String publishedAt;
    private String description;
    private String url;

    public Video(String videoId, String title, String channelTitle,
                 String publishedAt, String description, String url) {
        this.ts = Instant.now().toString();
        this.ss = "youtube-feeder";
        this.videoId = videoId;
        this.title = title;
        this.channelTitle = channelTitle;
        this.publishedAt = publishedAt;
        this.description = description;
        this.url = url;
    }

    public String getTs() { return ts; }
    public String getSs() { return ss; }
    public String getVideoId() { return videoId; }
    public String getTitle() { return title; }
    public String getChannelTitle() { return channelTitle; }
    public String getPublishedAt() { return publishedAt; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
}