package org.example.eventstorebuilder;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

public class ActiveMQConsumer {

    private static final String BROKER_URL = "tcp://localhost:61616";

    public void start() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            listenToTopic(session, "TicketmasterEvents", "TicketmasterFeeder");
            listenToTopic(session, "YouTubeEvents", "YoutubeFeeder");

            System.out.println("🟢 Listening events...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenToTopic(Session session, String topicName, String feederName) throws Exception {
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createConsumer(topic);

        consumer.setMessageListener(message -> {
            try {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String json = textMessage.getText();

                    saveEvent(topicName, feederName, json);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveEvent(String topicName, String feederName, String json) throws Exception {
        String date = LocalDate.now().toString().replace("-", "");

        String folderPath = "eventstore/" + topicName + "/" + feederName;
        Files.createDirectories(Paths.get(folderPath));

        String filePath = folderPath + "/" + date + ".events";

        Files.write(
                Paths.get(filePath),
                (json + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        System.out.println("💾 Saved event to " + filePath);
    }
}