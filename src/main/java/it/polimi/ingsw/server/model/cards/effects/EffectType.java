package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.virtual.JsonUtility;
import java.util.Arrays;

public enum EffectType {

    /**
     * This is the base effect of the card.
     */
    PRIMARY("primario"),

    /**
     * This is a secondary effect of the card that can be executed instead of the primary effect.
     */
    ALTERNATIVE("alternativo"),

    /**
     * This is a secondary effect that can be execute before or after the primary effect and the
     * "OPTIONAL_2" effect.
     */
    OPTIONAL_1("opzionale1"),

    /**
     * This is a secondary effect that can be execute before or after the primary effect and the
     * "OPTIONAL_1" effect.
     */
    OPTIONAL_2("opzionale2");

    /**
     * The name of the effect.
     */
    private final String name;

    EffectType(String name) {

        this.name = name;
    }

    /**
     * Gets the enum type EffectType corresponding to a string.
     *
     * @param request The string of the EffectType.
     * @return The searched EffectType.
     */
    public static EffectType ofString(String request) throws EffectException {

        return Arrays.stream(values())
                .filter(x -> JsonUtility.levenshteinDistance(request, x.name()) <= 0)
                .findFirst()
                .orElseThrow(() -> new EffectException("L'effetto selezionato non esiste."));
    }

    /**
     * Gets the enum type EffectType corresponding to a string.
     *
     * @param name The string of the EffectType.
     * @return The searched EffectType.
     */
    public static EffectType ofName(String name) throws EffectException {

        return Arrays.stream(values())
                .filter(x -> JsonUtility.levenshteinDistance(name.toLowerCase(), x.name) <= 0)
                .findFirst()
                .orElseThrow(() -> new EffectException("L'effetto selezionato non esiste."));
    }
}
