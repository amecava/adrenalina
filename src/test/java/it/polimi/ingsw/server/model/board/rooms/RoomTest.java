package it.polimi.ingsw.server.model.board.rooms;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.players.Player;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * Tests the rooms methods.
 */
class RoomTest {

    /**
     * Tests the unsupported operations.
     */
    @Test
    void getCurrentPosition() {

        Room room = new Room(Color.ROSSO);

        try {
            room.getCurrentPosition();

            fail();
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }

    /**
     * Tests if the Room getPlayers method return all the players in the selected room.
     */
    @Test
    void getPlayers() {
        Room room = new Room(Color.ROSSO);

        Square square1 = new Square(0, false);
        Square square2 = new Square(1, true);

        room.addSquare(square1);
        room.addSquare(square2);

        Player player1 = new Player("jacopo", Color.BLU);
        Player player2 = new Player("Amedeo", Color.VERDE);
        Player player3 = new Player("federico", Color.GIALLO);

        assertTrue(room.getPlayers().isEmpty());

        square1.addPlayer(player1);
        square2.addPlayer(player2);
        square2.addPlayer(player3);

        assertTrue(room.getPlayers().containsAll(Arrays.asList(player1, player2, player3)));
    }
}