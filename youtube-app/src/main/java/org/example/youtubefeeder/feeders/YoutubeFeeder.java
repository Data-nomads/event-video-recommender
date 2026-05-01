package org.example.youtubefeeder.feeders;

import org.example.youtubefeeder.model.Video;
import java.util.List;

public interface YoutubeFeeder {
    List<Video> feed();
}