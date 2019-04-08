package it.polimi.ingsw.model.cards.effects.atomic;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class PlayerDamageTest {

    @Test
    void execute() {

        AtomicEffect tester = new PlayerDamage(1);

        Player player = new Player("player", Color.GRAY);

        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.VIOLET);
        Player target3 = new Player("target3", Color.YELLOW);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(target1, target2, target3)));
        } catch (IllegalArgumentException e) {
            fail();
        }

        assertSame(
                target1.getBridge()
                        .getDamageBridge()
                        .getShots()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        assertSame(
                target2.getBridge()
                        .getDamageBridge()
                        .getShots()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        assertSame(
                target3.getBridge()
                        .getDamageBridge()
                        .getShots()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        Room room = new Room(Color.RED);
        Square square = new Square(room, 1);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(square)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(room)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        Player target4 = new Player("target4", Color.LIGHTBLUE);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(square, target4)));
        } catch (IllegalArgumentException e) {
            assertSame(
                    target4.getBridge()
                            .getDamageBridge()
                            .getShots()
                            .size(),
                    0
            );
        }
    }
}