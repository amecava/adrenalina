package it.polimi.ingsw.server.model.players;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Color {

    YELLOW("\u001b[33m", ":d-strutt-or3"),
    LIGHTBLUE("\u001b[36m", "banshee"),
    GRAY("\u001b[37m", "dozer"),
    VIOLET("\u001b[35m", "violetta"),
    GREEN("\u001b[32m", "sprog"),
    RED("\u001b[31m"),
    BLUE("\u001b[34m"),
    WHITE("\u001b[37m"),
    ALL("\u001b[0m");

    private final String ansiColor;
    private final String character;

    private static final Map<String, Color> map = new HashMap<>();

    static {

        Arrays.stream(values())
                .filter(x -> x.character != null)
                .forEach(x -> map.put(x.character, x));
    }

    Color(String ansiColor) {

        this.character = null;
        this.ansiColor = ansiColor;
    }

    Color(String ansiColor, String character) {

        this.character = character;
        this.ansiColor = ansiColor;
    }

    public String getCharacter() {

        return this.character;
    }

    public static Color ofCharacter(String character) {

        return map.get(character);
    }

    public static String ansiColor(String color) {

        return Color.valueOf(color).ansiColor;
    }
}
