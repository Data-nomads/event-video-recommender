package org.example.youtubefeeder.db;

import org.example.youtubefeeder.model.Video;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class VideosYoutubeStore {

    private final DbConnection dbConnection;

    public VideosYoutubeStore(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS youtube_videos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    video_id TEXT NOT NULL,
                    title TEXT NOT NULL,
                    channel_title TEXT,
                    published_at TEXT,
                    description TEXT,
                    url TEXT,
                    captured_at TEXT NOT NULL
                )
                """;

        try (Connection connection = dbConnection.connect();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException("Error creating youtube_videos table", e);
        }
    }

    public void saveVideos(List<Video> videos) {
        String sql = """
                INSERT INTO youtube_videos
                (video_id, title, channel_title, published_at, description, url, captured_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = dbConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Video video : videos) {
                statement.setString(1, video.getVideoId());
                statement.setString(2, video.getTitle());
                statement.setString(3, video.getChannelTitle());
                statement.setString(4, video.getPublishedAt());
                statement.setString(5, video.getDescription());
                statement.setString(6, video.getUrl());
                statement.setString(7, video.getCapturedAt());
                statement.executeUpdate();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error saving YouTube videos", e);
        }
    }
}