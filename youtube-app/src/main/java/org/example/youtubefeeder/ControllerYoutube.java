package org.example.youtubefeeder;

import org.example.youtubefeeder.adapters.YoutubeVideosProvider;
import org.example.youtubefeeder.db.VideosYoutubeStore;
import org.example.youtubefeeder.model.Video;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ControllerYoutube {

    private final YoutubeVideosProvider provider;
    private final VideosYoutubeStore store;

    public ControllerYoutube(YoutubeVideosProvider provider, VideosYoutubeStore store) {
        this.provider = provider;
        this.store = store;
    }

    public void run() {
        String json = provider.getVideosJson();
        List<Video> videos = parseVideos(json);

        store.createTableIfNotExists();
        store.saveVideos(videos);

        System.out.println("Saved " + videos.size() + " videos in SQLite");
    }

    private List<Video> parseVideos(String json) {
        List<Video> videos = new ArrayList<>();

        JSONObject root = new JSONObject(json);
        JSONArray items = root.getJSONArray("items");
        String capturedAt = LocalDateTime.now().toString();

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);

            if (!item.has("id") || !item.getJSONObject("id").has("videoId")) {
                continue;
            }

            String videoId = item.getJSONObject("id").getString("videoId");
            JSONObject snippet = item.getJSONObject("snippet");

            String title = snippet.optString("title");
            String channelTitle = snippet.optString("channelTitle");
            String publishedAt = snippet.optString("publishedAt");
            String description = snippet.optString("description");
            String url = "https://www.youtube.com/watch?v=" + videoId;

            Video video = new Video(
                    videoId,
                    title,
                    channelTitle,
                    publishedAt,
                    description,
                    url,
                    capturedAt
            );

            videos.add(video);
        }

        return videos;
    }
}