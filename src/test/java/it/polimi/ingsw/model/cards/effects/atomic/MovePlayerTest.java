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

        Room room = new Room(Color.RED);
        Square square = new Square(room, 1);
        Square destination = new Square(room, 2);

        square.addPlayer(target);

        tester.execute(player, new ArrayList<>(Arrays.asList(destination, target)));

        assertTrue(destination.getPlayers().contains(target));
        assertEquals(target.getCurrentPosition(), destination);

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(target)));
        } catch (ClassCastException e) {
            assertTrue(true);
        }

        try {
            tester.execute(player, new ArrayList<>(Arrays.asList(destination, square)));
        } catch (ClassCastException e) {
            assertTrue(true);
        }
    }
}