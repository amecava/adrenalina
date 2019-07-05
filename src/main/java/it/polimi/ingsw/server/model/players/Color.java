package it.polimi.ingsw.server.model.players;

import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.common.JsonUtility;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This enumeration links the every character name, with a string containing its color and the ansi
 * code corresponding to the color.
 */
public enum Color {

    /**
     * :DistruttOR3 - Giallo
     */
    GIALLO("Giallo", "\u001b[33m", ":DistruttOR3"),

    /**
     * Banshee - Azzurro
     */
    AZZURRO("Azzurro", "\u001b[36m", "Banshee"),

    /**
     * Dozer - Grigio
     */
    GRIGIO("Grigio", "\u001b[37m", "Dozer"),

    /**
     * Violetta - Viola
     */
    VIOLA("Viola", "\u001b[35m", "Violetta"),

    /**
     * Sprog - Verde
     */
    VERDE("Verde", "\u001b[32m", "Sprog"),

    /**
     * Rosso
     */
    ROSSO("Rosso", "\u001b[31m"),

    /**
     * Blu
     */
    BLU("Blu", "\u001b[34m"),

    /**
     * Bianco
     */
    BIANCO("Bianco", "\u001b[37m"),

    /**
     * Reset and neutral color.
     */
    ALL("All", "\u001b[0m");

    /**
     * The string representing the color.
     */
    private final String name;

    /**
     * The ansi color code.
     */
    private final String ansiColor;

    /**
     * The name of the character that is colored "name".
     */
    private final String character;

    /**
     * A map that links a String to the Color it represents.
     */
    private static final Map<String, Color> map = new HashMap<>();

    /**
     * Statically fills the map with the possible values.
     */
    static {

        Arrays.stream(values())
                .filter(x -> x.character != null)
                .forEach(x -> map.put(x.character, x));
    }

    /**
     * Builds the Color with the name and ansiColor
     *
     * @param name The name of the color.
     * @param ansiColor The ansiColor code of the color.
     */
    Color(String name, String ansiColor) {

        this.name = name;
        this.ansiColor = ansiColor;
        this.character = null;
    }

    /**
     * Builds the Color with the name, ansiColor and character.
     *
     * @param name The name of the color.
     * @param ansiColor The ansiColor code of the color.
     * @param character The character colored "color".
     */
    Color(String name, String ansiColor, String character) {

        this.name = name;
        this.character = character;
        this.ansiColor = ansiColor;
    }

    /**
     * Gets the name of Color.this.
     *
     * @return The name of Colo.this.
     */
    public String getName() {

        return this.name;
    }

    /**
     * Gets the name of the character of Color.this.
     *
     * @return The name of the character of Color.this.
     */
    public String getCharacter() {

        return this.character;
    }

    /**
     * Gets the Color of the character sent as a parameter.
     *
     * @param character The character that you want to know the color.
     * @return The Color of the character "character".
     */
    public static Color getColor(String character) {

        return map.get(character);
    }

    /**
     * Gets the Color that has the name "name".
     *
     * @param name The name of the color you want to get the Color.
     * @return The Color.
     */
    public static Color ofName(String name) {

        return Arrays.stream(values())
                .filter(x -> JsonUtility.levenshteinDistance(name.toLowerCase(), x.name) <= 1)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the color of the character sent as a parameter.
     *
     * @param characterName The name of the character.
     * @return The Color of the character.
     * @throws ColorException If the character sent as a parameter doesn't exixst.
     */
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
