package it.polimi.ingsw.client.view.console;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

class RegexJson {

    private static Map<String, String> inputMethod = new HashMap<>();
    private static Map<String, List<Triple<String, Pattern, String>>> regexConnection = new HashMap<>();

    static {

        InputStream in = ConsoleView.class.getClassLoader().getResourceAsStream("ConsoleInput.json");

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

        InputStream in = RegexJson.class.getClassLoader().getResourceAsStream("ConsoleInput.json");

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

    static JsonObject toJsonObject(String[] parts) {

        JsonObjectBuilder builder = Json.createObjectBuilder();

        String method = inputMethod.entrySet().stream()
                .filter(x -> levenshteinDistance(parts[0], x.getKey()) <= 3)
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Selezione non disponibile, riprova o digita help."));

        builder.add("method", method);

        int i = 0;

        List<Triple<String, Pattern, String>> patterns = regexConnection.get(method);

        String value = parts.length == 2 ? parts[1] : "";

        Matcher m = patterns.get(i).getSecond().matcher(value);

        while (m.lookingAt()) {

            builder.add(patterns.get(i).getFirst(), m.group(2));

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

    private static int levenshteinDistance(String input, String match) {

        input = input.toLowerCase();
        match = match.toLowerCase();

        int[] costs = new int[match.length() + 1];

        for (int i = 0; i <= input.length(); i++) {

            int lastValue = i;

            for (int j = 0; j <= match.length(); j++) {

                if (i == 0) {

                    costs[j] = j;

                } else if (j > 0) {

                    int newValue = costs[j - 1];

                    if (input.charAt(i - 1) != match.charAt(j - 1)) {

                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }

                    costs[j - 1] = lastValue;

                    lastValue = newValue;
                }
            }

            if (i > 0) {

                costs[match.length()] = lastValue;
            }
        }

        return costs[match.length()];
    }
}
