package org.example.ticketmasterfeeder.broker;

import com.google.gson.Gson;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.ticketmasterfeeder.model.Event;

public class TicketmasterPublisher {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "TicketmasterEvents";

    private final Gson gson;

    public TicketmasterPublisher() {
        this.gson = new Gson();
    }

    public void publish(Event event) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(topic);

            String jsonEvent = gson.toJson(event);

            TextMessage message = session.createTextMessage(jsonEvent);
            producer.send(message);

            System.out.println("Evento publicado en ActiveMQ: " + event.getName());

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            System.err.println("Error al conectar con ActiveMQ: " + e.getMessage());
        }
    }
}