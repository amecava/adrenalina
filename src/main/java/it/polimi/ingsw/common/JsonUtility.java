package it.polimi.ingsw.common;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * This class offers some static auxiliary method used in the view.
 */
public class JsonUtility {

    /**
     * Creates the JsonUtility class.
     */
    private JsonUtility() {

        //
    }

    /**
     * Static method to deserialize the JsonObject sent from the client.
     *
     * @param line JsonObject as a String.
     * @return deserialized JsonObject
     */
    public static JsonObject jsonDeserialize(String line) {

        return Json.createReader(new StringReader(line)).readObject();
    }

    /**
     * Method that deals with typos in the cli. The Levenshtein distance consists in how many steps
     * it takes to transform the string input into the string match.
     *
     * @param input Users's string
     * @param match String that is expected.
     * @return Levenshtein's distance.
     */
    public static int levenshteinDistance(String input, String match) {

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
