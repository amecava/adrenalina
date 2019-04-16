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

class PlayerDamageTest {

    @Test
    void execute() {

        AtomicEffect tester = new PlayerDamage();

        Player player = new Player("player", Color.GRAY);

        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.VIOLET);
        Player target3 = new Player("target3", Color.YELLOW);

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(target1, target2, target3)));

            assertSame(target1.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target2.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target3.getShots().get(0).getColor(), Color.GRAY);
        } catch (EffectTypeException e) {
            fail();
        }

        Square square = new Square(1);

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(square)));
        } catch (EffectTypeException e) {
            assertTrue(true);
        }

        Room room = new Room(Color.RED);

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(room)));
        } catch (EffectTypeException e) {
            assertTrue(true);
        }

        Player target4 = new Player("target4", Color.LIGHTBLUE);

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(square, target4)));
        } catch (EffectTypeException e) {
            assertSame(target4.getShots().size(), 0);
        }
    }
}