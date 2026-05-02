package org.example.youtubefeeder.feeders;

import org.example.youtubefeeder.model.Video;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class YoutubeVideosProvider implements YoutubeFeeder {

    private final Properties properties;

    public YoutubeVideosProvider(Properties properties) {
        this.properties = properties;
    }

    @Override
    public List<Video> feed() {
        String json = getVideosJson();
        return parseVideos(json);
    }

    private String getVideosJson() {
        try {
            String apiKey = properties.getProperty("youtube.api.key");
            String baseUrl = properties.getProperty("youtube.api.base.url");
            String query = properties.getProperty("youtube.search.query");
            String maxResults = properties.getProperty("youtube.search.maxResults");

            String urlString = baseUrl + "/search"
                    + "?part=snippet"
                    + "&q=" + query.replace(" ", "+")
                    + "&type=video"
                    + "&maxResults=" + maxResults
                    + "&key=" + apiKey;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            InputStream stream;
            if (responseCode >= 200 && responseCode < 300) {
                stream = connection.getInputStream();
            } else {
                stream = connection.getErrorStream();
                String error = readStream(stream);
                throw new RuntimeException("YouTube API error (" + responseCode + "): " + error);
            }

            return readStream(stream);

        } catch (Exception e) {
            throw new RuntimeException("Error calling YouTube API", e);
        }
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

            Video video = new Video(
                    videoId,
                    snippet.optString("title"),
                    snippet.optString("channelTitle"),
                    snippet.optString("publishedAt"),
                    snippet.optString("description"),
                    "https://www.youtube.com/watch?v=" + videoId,
                    capturedAt
            );

            videos.add(video);
        }

        return videos;
    }

    private String readStream(InputStream stream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return response.toString();
    }
}