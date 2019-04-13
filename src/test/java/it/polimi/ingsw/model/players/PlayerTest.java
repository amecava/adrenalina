package it.polimi.ingsw.model.players;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Board;
import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void movePlayer() {

        Board board = new Board.BoardBuilder(0).build();

        Player player = new Player("player", Color.GRAY);

        board.getRoom(0).getSquare(0).addPlayer(player);

        assertEquals(player.getCurrentPosition(), board.getRoom(0).getSquare(0));

        player.movePlayer(board.getRoom(1).getSquare(1));

        assertEquals(player.getCurrentPosition(), board.getRoom(1).getSquare(1));
        assertEquals(player.getOldPosition(), board.getRoom(0).getSquare(0));

        assertTrue(board.getRoom(1).getSquare(1).getPlayers().contains(player));
        assertFalse(board.getRoom(0).getSquare(0).getPlayers().contains(player));
    }
}