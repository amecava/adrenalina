package it.polimi.ingsw.server.model.players;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Color {

    YELLOW("\u001B[33m", ":D-STRUCT-OR"),
    LIGHTBLUE("\u001B[36m", "BANSHEE"),
    GRAY("\u001B[30m", "DOZER"),
    VIOLET("\u001B[35m", "VIOLET"),
    GREEN("\u001B[32m", "SPROG"),
    RED("\u001B[31m"),
    BLUE("\u001B[34m"),
    WHITE("\u001B[37m"),
    ALL("\u001B[0m");

    private final String ansiColor;
    private final String character;

    private static final Map<String, Color> map = new HashMap<>();

    static {

        Arrays.stream(values())
                .filter(x -> x.character != null)
                .forEach(x -> map.put(x.character, x));
    }

    Color(String ansiColor) {

        this.ansiColor = ansiColor;
        this.character = null;
    }

    Color(String ansiColor, String character) {

        this.ansiColor = ansiColor;
        this.character = character;
    }

    public String getAnsiColor() {

        return this.ansiColor;
    }

    public String getCharacter() {

        return this.character;
    }

    public static String ansiColor(String color) {

        return valueOf(color).getAnsiColor();
    }

    public static Color ofCharacter(String character) {

        return map.get(character);
    }
}
