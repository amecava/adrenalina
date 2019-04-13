package it.polimi.ingsw.model.cards.effects.atomic;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class RoomDamageTest {

    @Test
    void execute() {

        AtomicEffect tester = new RoomDamage();

        Player player = new Player("player", Color.GRAY);

        Room room = new Room(Color.RED);

        Square square1 = new Square(1);
        Square square2 = new Square(2);

        room.addSquare(square1);
        room.addSquare(square2);

        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.VIOLET);

        room.addSquare(square1);
        room.addSquare(square2);

        square1.addPlayer(target1);
        square2.addPlayer(target2);
        square2.addPlayer(player);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(room)));

            assertSame(target1.getBridge().getShots().get(0).getColor(), Color.GRAY);
            assertSame(target2.getBridge().getShots().get(0).getColor(), Color.GRAY);
            assertSame(player.getBridge().getShots().size(), 0);
        } catch (IllegalArgumentException e) {
            fail();
        }

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(square1)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(target1)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        Player target4 = new Player("target4", Color.LIGHTBLUE);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(room, target4)));
        } catch (IllegalArgumentException e) {
            assertSame(target4.getBridge().getShots().size(), 0);
        }
    }
}