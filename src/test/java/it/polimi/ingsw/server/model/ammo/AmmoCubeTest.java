package it.polimi.ingsw.server.model.ammo;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import org.junit.jupiter.api.Test;

class AmmoCubeTest {

    @Test
    void getColor() {

        AmmoCube tester = new AmmoCube(Color.GIALLO, false);

        assertEquals(tester.getColor(), Color.GIALLO);
    }

    @Test
    void isUsed() {

        AmmoCube tester = new AmmoCube(Color.GIALLO, false);

        assertFalse(tester.isUsed());
    }

    @Test
    void setUsed() {

        AmmoCube tester = new AmmoCube(Color.GIALLO, false);

        assertFalse(tester.isUsed());

        tester.setUsed(true);

        assertTrue(tester.isUsed());
    }
}