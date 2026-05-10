package org.example.businessunit.broker;

import com.google.gson.Gson;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.businessunit.datamart.RecommenderDatamart;
import org.example.businessunit.model.Event;
import org.example.businessunit.model.Video;

public class BusinessUnitConsumer {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private final RecommenderDatamart datamart;
    private final Gson gson;

    public BusinessUnitConsumer(RecommenderDatamart datamart) {
        this.datamart = datamart;
        this.gson = new Gson();
    }

    public void start() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();

            connection.setClientID("BusinessUnitApp");
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic ticketmasterTopic = session.createTopic("TicketmasterEvents");
            MessageConsumer ticketmasterConsumer = session.createDurableSubscriber(ticketmasterTopic, "TicketmasterBusinessSub");

            ticketmasterConsumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage textMessage) {
                        String json = textMessage.getText();
                        Event event = gson.fromJson(json, Event.class);
                        datamart.addEvent(event);
                    }
                } catch (Exception e) {
                    System.err.println("Error con evento de Ticketmaster: " + e.getMessage());
                }
            });

            Topic youtubeTopic = session.createTopic("YouTubeEvents");
            MessageConsumer youtubeConsumer = session.createDurableSubscriber(youtubeTopic, "YouTubeBusinessSub");

            youtubeConsumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage textMessage) {
                        String json = textMessage.getText();
                        Video video = gson.fromJson(json, Video.class);
                        datamart.addVideo(video);
                    }
                } catch (Exception e) {
                    System.err.println("Error con vídeo de YouTube: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("Error al conectar con ActiveMQ: " + e.getMessage());
        }
    }
}