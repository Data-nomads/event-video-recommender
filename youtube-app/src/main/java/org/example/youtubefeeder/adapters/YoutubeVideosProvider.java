package org.example.youtubefeeder.adapters;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class YoutubeVideosProvider {

    private final Properties properties;

    public YoutubeVideosProvider(Properties properties) {
        this.properties = properties;
    }

    public String getVideosJson() {
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