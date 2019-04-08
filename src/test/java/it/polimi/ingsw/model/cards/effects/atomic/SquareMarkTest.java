package it.polimi.ingsw.model.cards.effects.atomic;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class SquareMarkTest {

    @Test
    void execute() {

        AtomicEffect tester = new SquareMark(1);

        Player player = new Player("player", Color.GRAY);

        Room room = new Room(Color.RED);
        Square square1 = new Square(room, 1);
        Square square2 = new Square(room, 2);

        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.VIOLET);

        square1.addPlayer(target1);
        square1.addPlayer(target2);
        square2.addPlayer(player);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(square1, square2)));
        } catch (IllegalArgumentException e) {
            fail();
        }

        assertSame(
                target1.getBridge()
                        .getDamageBridge()
                        .getMarkers()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        assertSame(
                target2.getBridge()
                        .getDamageBridge()
                        .getMarkers()
                        .get(0)
                        .getColor(),
                Color.GRAY
        );

        assertSame(
                player.getBridge()
                        .getDamageBridge()
                        .getMarkers()
                        .size(),
                0
        );

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(target1, target2)));
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
            tester.execute(player, new ArrayList<>(Arrays.asList(square1, target4)));
        } catch (IllegalArgumentException e) {
            assertSame(
                    target4.getBridge()
                            .getDamageBridge()
                            .getMarkers()
                            .size(),
                    0
            );
        }
    }
}