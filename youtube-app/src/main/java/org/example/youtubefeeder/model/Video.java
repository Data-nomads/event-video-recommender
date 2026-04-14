package org.example.youtubefeeder.model;

public class Video {
    private String videoId;
    private String title;
    private String channelTitle;
    private String publishedAt;
    private String description;
    private String url;
    private String capturedAt;

    public Video(String videoId, String title, String channelTitle,
                 String publishedAt, String description, String url,
                 String capturedAt) {
        this.videoId = videoId;
        this.title = title;
        this.channelTitle = channelTitle;
        this.publishedAt = publishedAt;
        this.description = description;
        this.url = url;
        this.capturedAt = capturedAt;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getCapturedAt() {
        return capturedAt;
    }
}