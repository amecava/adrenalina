package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.virtual.JsonUtility;
import java.util.Arrays;

public enum EffectType {

    PRIMARY("primario"),
    ALTERNATIVE("alternativo"),
    OPTIONAL_1("opzionale1"),
    OPTIONAL_2("opzionale2");

    private final String name;

    EffectType(String name) {
        
        this.name = name;
    }

    public static EffectType ofString(String request) throws EffectException {

        return Arrays.stream(values())
                .filter(x -> JsonUtility.levenshteinDistance(request, x.name()) <= 0)
                .findFirst()
                .orElseThrow(() -> new EffectException("L'effetto selezionato non esiste."));
    }

    public static EffectType ofName(String name) throws EffectException {

        return Arrays.stream(values())
                .filter(x -> JsonUtility.levenshteinDistance(name.toLowerCase(), x.name) <= 0)
                .findFirst()
                .orElseThrow(() -> new EffectException("L'effetto selezionato non esiste."));
    }
}
