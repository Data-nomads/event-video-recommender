package org.example.ticketmasterfeeder.broker;

import com.google.gson.JsonObject;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.ticketmasterfeeder.model.Event;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class TicketmasterPublisher {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "TicketmasterEvents";

    public void publish(Event event) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(topic);

            // Creamos JSON manual (evita error con LocalDateTime)
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ts", java.time.Instant.now().toString());
            jsonObject.addProperty("ss", "TicketmasterFeeder");
            jsonObject.addProperty("id", event.getId());
            jsonObject.addProperty("name", event.getName());
            jsonObject.addProperty("date", event.getDate());
            jsonObject.addProperty("venue", event.getVenue());
            jsonObject.addProperty("capturedAt", event.getCapturedAt().toString());

            TextMessage message = session.createTextMessage(jsonObject.toString());

            producer.send(message);

            System.out.println("Event published to ActiveMQ: " + event.getName());

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}