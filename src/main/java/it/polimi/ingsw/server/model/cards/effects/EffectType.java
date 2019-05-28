package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.virtual.JsonUtility;
import java.util.Arrays;

public enum EffectType {

    PRIMARY,
    ALTERNATIVE,
    OPTIONAL_1,
    OPTIONAL_2;

    public static EffectType ofString(String request) throws EffectException {

        return Arrays.stream(values())
                .filter(x -> JsonUtility.levenshteinDistance(request, x.toString()) <= 3)
                .findFirst()
                .orElseThrow(() -> new EffectException("L'effetto selezionato non esiste."));
    }
}
