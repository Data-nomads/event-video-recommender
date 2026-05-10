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
        List<Video> allVideos = new ArrayList<>();

        String queriesString = properties.getProperty("youtube.search.query");
        if (queriesString == null || queriesString.isEmpty()) {
            System.err.println("No se encontraron artistas en application.properties");
            return allVideos;
        }

        // Separamos los artistas por las comas
        String[] artists = queriesString.split(",");

        for (String artist : artists) {
            String cleanArtistName = artist.trim();
            System.out.println("Buscando en YouTube los vídeos más vistos de: " + cleanArtistName);

            try {
                String json = getVideosJson(cleanArtistName);
                allVideos.addAll(parseVideos(json));
            } catch (Exception e) {
                System.err.println("Error buscando a " + cleanArtistName + ": " + e.getMessage());
            }
        }

        return allVideos;
    }

    private String getVideosJson(String artistQuery) {
        try {
            String apiKey = properties.getProperty("youtube.api.key");
            String baseUrl = properties.getProperty("youtube.api.base.url");
            String maxResults = properties.getProperty("youtube.search.maxResults");

            // URL con el parámetro viewCount añadido para obtener los más populares
            String urlString = baseUrl + "/search"
                    + "?part=snippet"
                    + "&q=" + artistQuery.replace(" ", "+")
                    + "&type=video"
                    + "&order=viewCount"
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
            throw new RuntimeException("Error llamando a la API de YouTube", e);
        }
    }

    private List<Video> parseVideos(String json) {
        List<Video> videos = new ArrayList<>();

        JSONObject root = new JSONObject(json);
        if (!root.has("items")) return videos;

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