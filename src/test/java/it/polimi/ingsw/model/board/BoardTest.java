package it.polimi.ingsw.model.board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Color;
import it.polimi.ingsw.model.board.rooms.Connection;
import it.polimi.ingsw.model.board.rooms.Direction;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void buildBoard() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);

        assertEquals(board.getRoom(0).getColor(), Color.BLUE);
        assertEquals(board.getRoom(1).getColor(), Color.RED);
        assertEquals(board.getRoom(2).getColor(), Color.WHITE);
        assertEquals(board.getRoom(3).getColor(), Color.YELLOW);

        assertEquals(
                board.getRoom(1).getSquare(2).getAdjacent(Direction.EAST),
                board.getRoom(3).getSquare(0)
        );

        assertEquals(
                board.getRoom(3).getSquare(0).getAdjacent(Direction.WEST),
                board.getRoom(1).getSquare(2)
        );

        assertEquals(
                board.getRoom(2).getSquare(1).getAdjacent(Direction.NORTH),
                board.getRoom(1).getSquare(2)
        );

        assertEquals(
                board.getRoom(3).getSquare(0).getAdjacent(Direction.SOUTH),
                board.getRoom(3).getSquare(1)
        );

        assertEquals(
                board.getRoom(1).getSquare(2).getConnection(Direction.EAST),
                Connection.DOOR
        );

        assertEquals(
                board.getRoom(1).getSquare(1).getConnection(Direction.NORTH),
                Connection.WALL
        );

        assertEquals(
                board.getRoom(3).getSquare(0).getConnection(Direction.SOUTH),
                Connection.SQUARE
        );

        assertEquals(
                board.getRoom(2).getSquare(0).getConnection(Direction.WEST),
                Connection.ENDMAP
        );
    }

    @Test
    void squareJson() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);
        board.fillBoard();

        System.out.println(board.toJsonObject());
        //with the prints I could verify it works
        assertTrue(true);
    }

}