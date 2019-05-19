package it.polimi.ingsw.server.model.players;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Color {

    YELLOW(":D-STRUCT-OR"),
    LIGHTBLUE("BANSHEE"),
    GRAY("DOZER"),
    VIOLET("VIOLET"),
    GREEN("SPROG"),
    RED,
    BLUE,
    WHITE,
    ALL;

    private final String character;

    private static final Map<String, Color> map = new HashMap<>();

    static {

        Arrays.stream(values())
                .filter(x -> x.character != null)
                .forEach(x -> map.put(x.character, x));
    }

    Color() {

        this.character = null;
    }

    Color(String character) {

        this.character = character;
    }

    public String getCharacter() {

        return this.character;
    }

    public static Color ofCharacter(String character) {

        return map.get(character);
    }
}
