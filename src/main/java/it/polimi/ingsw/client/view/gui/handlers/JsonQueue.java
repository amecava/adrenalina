package it.polimi.ingsw.client.view.gui.handlers;

import java.util.ArrayDeque;
import java.util.Queue;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Main class for creating a bridge between the gui and the server.
 */
public class JsonQueue {

    /**
     * main queue for creating the bridge between the gui and the server
     */
    private static final Queue<JsonObject> queue = new ArrayDeque<>();
    /**
     * builder for creating the correct object to send to the server
     */
    private static JsonObjectBuilder builder = Json.createObjectBuilder();

    /**
     * private constructor
     */
    private JsonQueue() {

        //
    }

    /**
     * @return The queue on which the connection is pending
     */
    public static Queue<JsonObject> getQueue() {

        return queue;
    }

    /**
     * adds a new information to the object that will be sent to the server
     *
     * @param key key of the json object
     * @param value linked with the key
     */
    public static void add(String key, String value) {

        builder.add(key, value);
    }

    /**
     * sends the json object from the queue to the server waking up the sending client  connection
     * thread
     */
    public static void send() {

        synchronized (queue) {

            queue.add(builder.build());

            builder = Json.createObjectBuilder();

            queue.notifyAll();
        }
    }

}
