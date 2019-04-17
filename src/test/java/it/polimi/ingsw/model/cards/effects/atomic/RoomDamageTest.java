package it.polimi.ingsw.model.cards.effects.atomic;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.exceptions.effects.EffectTypeException;
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

        Square square1 = new Square(1, false);
        Square square2 = new Square(2, true);

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
            tester.execute(player, new AtomicTarget(Arrays.asList(room)));

            assertSame(target1.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target2.getShots().get(0).getColor(), Color.GRAY);
            assertSame(player.getShots().size(), 0);
        } catch (EffectTypeException e) {
            fail();
        }

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(square1)));
        } catch (EffectTypeException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(target1)));
        } catch (EffectTypeException e) {
            assertTrue(true);
        }

        Player target4 = new Player("target4", Color.LIGHTBLUE);

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(room, target4)));
        } catch (EffectTypeException e) {
            assertSame(target4.getShots().size(), 0);
        }
    }
}