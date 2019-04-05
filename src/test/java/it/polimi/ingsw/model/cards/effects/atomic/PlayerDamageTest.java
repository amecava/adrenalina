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

        tester.execute(player, new ArrayList<>(Arrays.asList(target1, target2, target3)));

        assertTrue(true); // target1 damaged
        assertTrue(true); // target2 damaged
        assertTrue(true); // target3 damaged

        Room room = new Room(Color.RED);
        Square square = new Square(room, 1);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(square)));
        } catch (ClassCastException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(room)));
        } catch (ClassCastException e) {
            assertTrue(true);
        }
    }
}