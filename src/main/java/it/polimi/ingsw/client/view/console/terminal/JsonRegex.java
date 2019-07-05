package it.polimi.ingsw.client.view.console.terminal;

import it.polimi.ingsw.client.view.console.ConsoleView;
import it.polimi.ingsw.common.JsonUtility;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * This class handles the terminal user input parsing.
 */
public class JsonRegex {

    /**
     * Private constructor to hide the public implicit one.
     */
    private JsonRegex() {

        //
    }

    /**
     * The info methods that the user can type and use.
     */
    private static final List<String> info = new ArrayList<>();
    /**
     * The game methods that the user can type and use.
     */
    private static final List<String> commands = new ArrayList<>();

    /**
     * This Map maps what the user types with the corresponding server method.
     */
    private static Map<String, String> inputMethod = new HashMap<>();
    /**
     * This Map maps the methods with the corresponding regexes.
     */
    private static Map<String, List<Triple<String, Pattern, String>>> regexConnection = new HashMap<>();

    static {

        InputStream in = ConsoleView.class.getClassLoader().getResourceAsStream("Commands.json");

        JsonArray object = Json.createReader(in).readArray();

        object.stream()
                .map(JsonValue::asJsonObject)
                .forEach(x ->

                        x.getJsonArray("input").stream()
                                .map(JsonValue::toString)
                                .forEach(y -> inputMethod.put(y, x.getString("method")))
                );
    }

    static {

        InputStream in = JsonRegex.class.getClassLoader().getResourceAsStream("Commands.json");

        JsonArray object = Json.createReader(in).readArray();

        object.stream()
                .map(JsonValue::asJsonObject)
                .forEach(x -> {

                    regexConnection.put(x.getString("method"), new ArrayList<>());

                    x.getJsonArray("value").stream()
                            .map(JsonValue::asJsonObject)
                            .forEach(y ->

                                    regexConnection.get(x.getString("method"))
                                            .add(new Triple<>(
                                                    y.getString("name"),
                                                    Pattern.compile(y.getString("regex")),
                                                    y.getString("error")
                                            ))
                            );
                });
    }

    /**
     * This method updates the client state.
     *
     * @param object The JsonObject containing the state information.
     */
    public static void updateState(JsonObject object) {

        info.clear();
        commands.clear();

        object.getJsonArray("info").stream()
                .map(JsonValue::toString)
                .forEach(x -> info.add(x.substring(1, x.length() - 1)));

        object.getJsonArray("methods").stream()
                .map(JsonValue::toString)
                .forEach(x -> commands.add(x.substring(1, x.length() - 1)));
    }

    /**
     * This methods calculates the info methods of the actual client state.
     *
     * @return The list of commands.
     */
    static List<String> getInfo() {

        return inputMethod.entrySet().stream()
                .filter(x -> info.stream().anyMatch(y -> y.equals(x.getValue())))
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * This methods calculates the game methods of the actual client state.
     *
     * @return The list of commands.
     */
    static List<String> getCommands() {

        return inputMethod.entrySet().stream()
                .filter(x -> commands.stream().anyMatch(y -> y.equals(x.getValue())))
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * This method parses the user input transforming it in a JsonObject.
     *
     * @param parts The user input divided in key word and value.
     * @return The JsonObject to be sent to the server.
     */
    public static JsonObject toJsonObject(String[] parts) {

        JsonObjectBuilder builder = Json.createObjectBuilder();

        String method = inputMethod.entrySet().stream()
                .filter(x -> JsonUtility.levenshteinDistance(parts[0], x.getKey()) <= 3)
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Selezione non disponibile, riprova o digita help."));

        if (info.stream().noneMatch(x -> x.equals(method))
                && commands.stream().noneMatch(x -> x.equals(method))) {

            throw new NoSuchElementException("C'è un tempo e un luogo per ogni cosa, ma non ora.");
        }

        builder.add("method", method);

        int i = 0;

        List<Triple<String, Pattern, String>> patterns = regexConnection.get(method);

        String value = parts.length == 2 ? parts[1] : "";

        Matcher m = patterns.get(i).getSecond().matcher(value);

        while (m.lookingAt()) {

            builder.add(patterns.get(i).getFirst(), m.group(2) != null ? m.group(2) : "");

            value = value.substring(m.end());

            i++;

            if (i >= patterns.size()) {

                break;
            }

            m = patterns.get(i).getSecond().matcher(value);

        }

        if (i < patterns.size()) {

            throw new NoSuchElementException(patterns.get(i).getThird());

        } else if (value.length() > 0) {

            throw new NoSuchElementException(patterns.get(i - 1).getThird());
        }

        return builder.build();
    }

    /**
     * A triple set
     *
     * @param <T> The first element of the triple.
     * @param <U> The second element of the triple.
     * @param <V> The third element of the triple.
     */
    private static class Triple<T, U, V> {

        private final T first;
        private final U second;
        private final V third;

        Triple(T first, U second, V third) {

            this.first = first;
            this.second = second;
            this.third = third;
        }

        T getFirst() {

            return this.first;
        }

        U getSecond() {

            return this.second;
        }

        V getThird() {

            return this.third;
        }
    }
}
