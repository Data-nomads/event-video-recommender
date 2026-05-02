package org.example.youtubefeeder.broker;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.youtubefeeder.model.Video;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class YoutubePublisher {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "YouTubeEvents";

    private final Gson gson;

    public YoutubePublisher() {
        this.gson = new Gson();
    }

    public void publish(Video video) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            MessageProducer producer = session.createProducer(topic);

            String jsonVideo = gson.toJson(video);
            TextMessage message = session.createTextMessage(jsonVideo);

            producer.send(message);

            System.out.println("Video published to ActiveMQ: " + video.getTitle());

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            throw new RuntimeException("Error publishing YouTube video to ActiveMQ", e);
        }
    }
}