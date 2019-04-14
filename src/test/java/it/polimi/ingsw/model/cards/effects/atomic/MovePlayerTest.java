package it.polimi.ingsw.model.cards.effects.atomic;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class MovePlayerTest {

    @Test
    void execute() {

        AtomicEffect tester = new MovePlayer();

        Player player = new Player("player", Color.GRAY);
        Player target = new Player("target", Color.GREEN);

        Square square = new Square(1);
        Square destination = new Square(2);

        square.addPlayer(target);

        try {
            tester.execute(player, new AtomicTarget(destination, Arrays.asList(target)));

            assertTrue(destination.getPlayers().contains(target));
            assertEquals(target.getCurrentPosition(), destination);
        } catch (IllegalArgumentException e) {
            fail();
        }

        try {
            tester.execute(player, new AtomicTarget(Arrays.asList(target)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new AtomicTarget(destination, Arrays.asList(square)));
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}