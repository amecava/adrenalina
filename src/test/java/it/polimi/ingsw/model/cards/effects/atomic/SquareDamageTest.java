package it.polimi.ingsw.model.cards.effects.atomic;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.effects.EffectTypeException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class SquareDamageTest {

    private EffectHandler effectHandler = new EffectHandler();


    @Test
    void execute() {

        AtomicEffect tester = new SquareDamage();

        Player player = new Player("player", Color.GRAY, this.effectHandler);

        Square square1 = new Square(1, false);
        Square square2 = new Square(2, true);

        Player target1 = new Player("target1", Color.GREEN, this.effectHandler);
        Player target2 = new Player("target2", Color.VIOLET, this.effectHandler);

        square1.addPlayer(target1);
        square1.addPlayer(target2);
        square2.addPlayer(player);

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(square1, square2)));

            assertSame(target1.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target2.getShots().get(0).getColor(), Color.GRAY);
            assertSame(player.getShots().size(), 0);
        } catch (EffectTypeException e) {
            fail();
        }

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(target1, target2)));
        } catch (EffectTypeException e) {
            assertTrue(true);
        }

        Room room = new Room(Color.RED);

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(room)));
        } catch (EffectTypeException e) {
            assertTrue(true);
        }

        Player target4 = new Player("target4", Color.LIGHTBLUE, this.effectHandler);

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(square1, target4)));
        } catch (EffectTypeException e) {
            assertSame(target4.getShots().size(), 0);
        }
    }
}