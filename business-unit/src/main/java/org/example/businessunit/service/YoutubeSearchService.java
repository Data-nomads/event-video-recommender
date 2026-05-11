package org.example.businessunit.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class YoutubeSearchService {

    private final String apiKey;

    private static final String BASE_URL =
            "https://www.googleapis.com/youtube/v3/search";

    public YoutubeSearchService() {

        try {

            Properties properties = new Properties();

            properties.load(new FileInputStream(
                    "youtube-app/src/main/resources/application.properties"
            ));

            this.apiKey = properties.getProperty("youtube.api.key");

        } catch (Exception e) {

            throw new RuntimeException("Error loading YouTube API key", e);
        }
    }

    public List<String> searchVideos(String artistName) {

        List<String> videos = new ArrayList<>();

        try {

            String query = URLEncoder.encode(
                    artistName,
                    StandardCharsets.UTF_8
            );

            String urlString = BASE_URL
                    + "?part=snippet"
                    + "&q=" + query
                    + "&type=video"
                    + "&order=viewCount"
                    + "&maxResults=5"
                    + "&key=" + apiKey;

            URL url = new URL(urlString);

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            StringBuilder response = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JsonObject root =
                    JsonParser.parseString(response.toString())
                            .getAsJsonObject();

            JsonArray items = root.getAsJsonArray("items");

            for (int i = 0; i < items.size(); i++) {

                JsonObject snippet = items.get(i)
                        .getAsJsonObject()
                        .getAsJsonObject("snippet");

                String title = snippet.get("title")
                        .getAsString()
                        .replace("&#39;", "'")
                        .replace("&amp;", "&")
                        .replace("&quot;", "\"");

                videos.add(title);
            }

        } catch (Exception e) {

            System.err.println(
                    "Error buscando vídeos en YouTube: "
                            + e.getMessage()
            );
        }

        return videos;
    }
}