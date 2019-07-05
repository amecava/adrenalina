package it.polimi.ingsw.server.model.ammo;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import org.junit.jupiter.api.Test;

/**
 * Tests the AmmoCube creating and modification.
 */
class AmmoCubeTest {

    /**
     * Tests the AmmmoCube getColor method.
     */
    @Test
    void getColor() {

        AmmoCube tester = new AmmoCube(Color.GIALLO, false);

        assertEquals(tester.getColor(), Color.GIALLO);
    }

    /**
     * Tests that new AmmoCubes are not already used.
     */
    @Test
    void isUsed() {

        AmmoCube tester = new AmmoCube(Color.GIALLO, false);

        assertFalse(tester.isUsed());
    }

    /**
     * Tests the AmmoCube setUsed method.
     */
    @Test
    void setUsed() {

        AmmoCube tester = new AmmoCube(Color.GIALLO, false);

        assertFalse(tester.isUsed());

        tester.setUsed(true);

        assertTrue(tester.isUsed());
    }
}