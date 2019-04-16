package it.polimi.ingsw.model.players.bridges;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import org.junit.jupiter.api.Test;

class DamageBridgeTest {

    @Test
    void appendShot() {

        DamageBridge damageBridge = new DamageBridge();

        damageBridge.appendShot(Color.RED);

        assertEquals(damageBridge.getShots().stream().filter(x -> x.getColor().equals(Color.RED))
                .count(), 1);
        assertEquals(damageBridge.getShots().stream().filter(x -> !x.getColor().equals(Color.RED))
                .count(), 0);

        damageBridge.appendShot(Color.BLUE);

        assertEquals(damageBridge.getShots().stream().filter(x -> x.getColor().equals(Color.RED))
                .count(), 1);
        assertEquals(damageBridge.getShots().stream().filter(x -> x.getColor().equals(Color.BLUE))
                .count(), 1);

        damageBridge.appendMark(Color.RED);
        damageBridge.appendMark(Color.RED);

        assertEquals(damageBridge.getShots().stream().filter(x -> x.getColor().equals(Color.RED))
                .count(), 1);
        assertEquals(damageBridge.getShots().stream().filter(x -> x.getColor().equals(Color.BLUE))
                .count(), 1);

        damageBridge.appendShot(Color.RED);

        assertEquals(damageBridge.getShots().stream().filter(x -> x.getColor().equals(Color.RED))
                .count(), 4);
        assertEquals(damageBridge.getShots().stream().filter(x -> x.getColor().equals(Color.BLUE))
                .count(), 1);
    }

    @Test
    void appendMark() {

        DamageBridge damageBridge = new DamageBridge();

        assertTrue(damageBridge.getMarks().isEmpty());

        damageBridge.appendMark(Color.RED);
        damageBridge.appendMark(Color.RED);

        assertEquals(damageBridge.getMarks().stream().filter(x -> x.getColor().equals(Color.RED))
                .count(), 2);
        assertEquals(damageBridge.getMarks().stream().filter(x -> !x.getColor().equals(Color.RED))
                .count(), 0);

        damageBridge.appendMark(Color.RED);
        damageBridge.appendMark(Color.RED);

        assertEquals(damageBridge.getMarks().stream().filter(x -> x.getColor().equals(Color.RED))
                .count(), 3);
        assertEquals(damageBridge.getMarks().stream().filter(x -> !x.getColor().equals(Color.RED))
                .count(), 0);
    }

    @Test
    void isDead() {

        DamageBridge damageBridge = new DamageBridge();

        for (int i = 0; i < 11; i++) {
            damageBridge.appendShot(Color.RED);
        }

        assertTrue(damageBridge.isDead());
    }

    @Test
    void checkAdrenalin() {

        DamageBridge damageBridge = new DamageBridge();

        damageBridge.appendShot(Color.RED);

        assertEquals(damageBridge.checkAdrenalin(), Adrenalin.NORMAL);

        for (int i = 0; i < 3; i++) {
            damageBridge.appendShot(Color.RED);
        }

        assertEquals(damageBridge.checkAdrenalin(), Adrenalin.FIRSTADRENALIN);

        for (int i = 0; i < 4; i++) {
            damageBridge.appendShot(Color.RED);
        }

        assertEquals(damageBridge.checkAdrenalin(), Adrenalin.SECONDADRENALIN);
    }
}