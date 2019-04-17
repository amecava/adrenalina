package it.polimi.ingsw.model.board.rooms;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class RoomTest {

    @Test
    void getCurrentPosition() {

        Room room = new Room(Color.RED);

        try {
            room.getCurrentPosition();

            fail();
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }

    @Test
    void getPlayers() {
        EffectHandler effectHandler = new EffectHandler();
        Room room = new Room(Color.RED);

        Square square1 = new Square(0, false);
        Square square2 = new Square(1, true);

        room.addSquare(square1);
        room.addSquare(square2);

        Player player1 = new Player("jacopo", Color.BLUE, effectHandler);
        Player player2 = new Player("Amedeo", Color.GREEN, effectHandler);
        Player player3 = new Player("federico", Color.YELLOW, effectHandler);

        assertTrue(room.getPlayers().isEmpty());

        square1.addPlayer(player1);
        square2.addPlayer(player2);
        square2.addPlayer(player3);

        assertTrue(room.getPlayers().containsAll(Arrays.asList(player1, player2, player3)));
    }
}