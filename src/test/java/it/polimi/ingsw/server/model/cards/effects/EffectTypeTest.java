package it.polimi.ingsw.server.model.cards.effects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests the correctness of valueOf method with a String.
 */
class EffectTypeTest {

    /**
     * Tests valueOf in EffectType enum.
     */
    @Test
    void effectType() {

        assertEquals(EffectType.valueOf("OPTIONAL_1"), EffectType.OPTIONAL_1);
    }

}