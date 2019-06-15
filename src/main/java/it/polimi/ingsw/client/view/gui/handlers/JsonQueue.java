package it.polimi.ingsw.client.view.gui.handlers;

import java.util.ArrayDeque;
import java.util.Queue;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class JsonQueue {

    private static final Queue<JsonObject> queue = new ArrayDeque<>();
    private static JsonObjectBuilder builder = Json.createObjectBuilder();

    private JsonQueue() {

        //
    }

    public static Queue<JsonObject> getQueue() {

        return queue;
    }

    public static void add(String key, String value) {

        builder.add(key, value);
    }

    public static void send() {

        synchronized (queue) {

            queue.add(builder.build());

            builder = Json.createObjectBuilder();

            queue.notifyAll();
        }
    }

}
