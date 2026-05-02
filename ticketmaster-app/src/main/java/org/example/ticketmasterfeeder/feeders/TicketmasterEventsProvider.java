package org.example.ticketmasterfeeder.feeders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.ticketmasterfeeder.model.Event;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketmasterEventsProvider {

    private static final String API_KEY = "d9GCFWkvApHP74E5MO7n8lR4guGFxFjw";
    private static final String URL_BASE = "https://app.ticketmaster.com/discovery/v2/events.json";

    private final OkHttpClient client;

    public TicketmasterEventsProvider() {
        this.client = new OkHttpClient();
    }

    public List<Event> getEvents() {
        List<Event> eventsList = new ArrayList<>();

        String url = URL_BASE + "?apikey=" + API_KEY + "&countryCode=ES";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Error: " + response.code());
                return eventsList;
            }

            String jsonResponse = response.body().string();
            eventsList = parseJsonResponse(jsonResponse);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return eventsList;
    }

    private List<Event> parseJsonResponse(String json) {
        List<Event> parsedEvents = new ArrayList<>();

        JsonObject rootObject = JsonParser.parseString(json).getAsJsonObject();

        if (!rootObject.has("_embedded")) {
            return parsedEvents;
        }

        JsonObject embedded = rootObject.getAsJsonObject("_embedded");
        JsonArray eventsArray = embedded.getAsJsonArray("events");

        for (JsonElement element : eventsArray) {
            JsonObject eventObj = element.getAsJsonObject();

            String id = getSafeString(eventObj, "id", "ID_DESCONOCIDO");
            String name = getSafeString(eventObj, "name", "Nombre no disponible");

            String date = "Fecha no disponible";
            if (eventObj.has("dates") && !eventObj.get("dates").isJsonNull()) {
                JsonObject datesObj = eventObj.getAsJsonObject("dates");
                if (datesObj.has("start") && !datesObj.get("start").isJsonNull()) {
                    JsonObject startObj = datesObj.getAsJsonObject("start");
                    date = getSafeString(startObj, "localDate", "Fecha no disponible");
                }
            }

            String venue = "Recinto no disponible";
            if (eventObj.has("_embedded") && !eventObj.get("_embedded").isJsonNull()) {
                JsonObject embeddedObj = eventObj.getAsJsonObject("_embedded");
                if (embeddedObj.has("venues") && !embeddedObj.get("venues").isJsonNull()) {
                    JsonArray venuesArray = embeddedObj.getAsJsonArray("venues");
                    if (!venuesArray.isEmpty()) {
                        JsonObject firstVenue = venuesArray.get(0).getAsJsonObject();
                        venue = getSafeString(firstVenue, "name", "Recinto no disponible");
                    }
                }
            }

            LocalDateTime capturedAt = LocalDateTime.now();
            Event event = new Event(id, name, date, venue, capturedAt);
            parsedEvents.add(event);
        }

        return parsedEvents;
    }

    private String getSafeString(JsonObject obj, String key, String defaultValue) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return defaultValue;
    }
}