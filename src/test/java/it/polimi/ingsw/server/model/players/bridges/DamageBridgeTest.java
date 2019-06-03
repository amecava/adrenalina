package it.polimi.ingsw.server.model.players.bridges;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import org.junit.jupiter.api.Test;

class DamageBridgeTest {

    @Test
    void appendShot() {

        DamageBridge damageBridge = new DamageBridge();

        damageBridge.appendShot(Color.ROSSO, true);

        assertEquals(damageBridge.getShots().stream().filter(x -> x.equals(Color.ROSSO))
                .count(), 1);
        assertEquals(damageBridge.getShots().stream().filter(x -> !x.equals(Color.ROSSO))
                .count(), 0);

        damageBridge.appendShot(Color.BLU, true);

        assertEquals(damageBridge.getShots().stream().filter(x -> x.equals(Color.ROSSO))
                .count(), 1);
        assertEquals(damageBridge.getShots().stream().filter(x -> x.equals(Color.BLU))
                .count(), 1);

        damageBridge.appendMark(Color.ROSSO);
        damageBridge.appendMark(Color.ROSSO);

        assertEquals(damageBridge.getShots().stream().filter(x -> x.equals(Color.ROSSO))
                .count(), 1);
        assertEquals(damageBridge.getShots().stream().filter(x -> x.equals(Color.BLU))
                .count(), 1);

        damageBridge.appendShot(Color.ROSSO, true);

        assertEquals(damageBridge.getShots().stream().filter(x -> x.equals(Color.ROSSO))
                .count(), 4);
        assertEquals(damageBridge.getShots().stream().filter(x -> x.equals(Color.BLU))
                .count(), 1);
    }

    @Test
    void appendMark() {

        DamageBridge damageBridge = new DamageBridge();

        assertTrue(damageBridge.getMarks().isEmpty());

        damageBridge.appendMark(Color.ROSSO);
        damageBridge.appendMark(Color.ROSSO);

        assertEquals(damageBridge.getMarks().stream().filter(x -> x.equals(Color.ROSSO))
                .count(), 2);
        assertEquals(damageBridge.getMarks().stream().filter(x -> !x.equals(Color.ROSSO))
                .count(), 0);

        damageBridge.appendMark(Color.ROSSO);
        damageBridge.appendMark(Color.ROSSO);

        assertEquals(damageBridge.getMarks().stream().filter(x -> x.equals(Color.ROSSO))
                .count(), 3);
        assertEquals(damageBridge.getMarks().stream().filter(x -> !x.equals(Color.ROSSO))
                .count(), 0);
    }

    @Test
    void isDead() {

        DamageBridge damageBridge = new DamageBridge();

        for (int i = 0; i < 11; i++) {
            damageBridge.appendShot(Color.ROSSO, true);
        }

        assertTrue(damageBridge.isDead());
    }

    @Test
    void checkAdrenalin() {

        DamageBridge damageBridge = new DamageBridge();

        damageBridge.appendShot(Color.ROSSO, true);

        assertEquals(damageBridge.getAdrenalin(), Adrenalin.NORMAL);

        for (int i = 0; i < 3; i++) {
            damageBridge.appendShot(Color.ROSSO, true);
        }

        assertEquals(damageBridge.getAdrenalin(), Adrenalin.FIRSTADRENALIN);

        for (int i = 0; i < 4; i++) {
            damageBridge.appendShot(Color.ROSSO, true);
        }

        assertEquals(damageBridge.getAdrenalin(), Adrenalin.SECONDADRENALIN);
    }
}