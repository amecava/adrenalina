package it.polimi.ingsw.server.model.players;

import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.common.JsonUtility;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Color {

    GIALLO("Giallo", "\u001b[33m", ":DistruttOR3"),
    AZZURRO("Azzurro", "\u001b[36m", "Banshee"),
    GRIGIO("Grigio", "\u001b[37m", "Dozer"),
    VIOLA("Viola", "\u001b[35m", "Violetta"),
    VERDE("Verde", "\u001b[32m", "Sprog"),
    ROSSO("Rosso", "\u001b[31m"),
    BLU("Blu", "\u001b[34m"),
    BIANCO("Bianco", "\u001b[37m"),
    ALL("All", "\u001b[0m");

    private final String name;
    private final String ansiColor;
    private final String character;

    private static final Map<String, Color> map = new HashMap<>();

    static {

        Arrays.stream(values())
                .filter(x -> x.character != null)
                .forEach(x -> map.put(x.character, x));
    }

    Color(String name, String ansiColor) {

        this.name = name;
        this.ansiColor = ansiColor;
        this.character = null;
    }

    Color(String name, String ansiColor, String character) {

        this.name = name;
        this.character = character;
        this.ansiColor = ansiColor;
    }

    public String getName() {

        return this.name;
    }

    public String getCharacter() {

        return this.character;
    }

    public static Color getColor(String character) {

        return map.get(character);
    }

    public static Color ofName(String name) {

        return Arrays.stream(values())
                .filter(x -> JsonUtility.levenshteinDistance(name.toLowerCase(), x.name) <= 1)
                .findFirst()
                .orElse(null);
    }


    public static Color ofCharacter(String characterName) throws ColorException {

        return Arrays.stream(values())
                .filter(x -> x.character != null)
                .filter(x -> JsonUtility.levenshteinDistance(characterName, x.character) <= 3)
                .findFirst()
                .orElseThrow(() -> new ColorException("Il personaggio selezionato non esiste."));
    }

    public static String ansiColorOf(String color) {

        return Color.valueOf(color).ansiColor;
    }
}
