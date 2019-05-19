package it.polimi.ingsw.server.model.cards.effects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EffectTypeTest {

    @Test
    void effectType() {

        assertEquals(EffectType.valueOf("OPTIONAL_1"), EffectType.OPTIONAL_1);
    }

}