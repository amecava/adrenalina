package it.polimi.ingsw.model.board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Connection;
import it.polimi.ingsw.model.board.rooms.Direction;
import org.junit.jupiter.api.Test;

class BoardBuilderTest {

    @Test
    void buildBoard() {
        Board board = new Board.BoardBuilder(0).build();

        assertEquals(board.getRoomsList(0).getColor(), Color.BLUE);
        assertEquals(board.getRoomsList(1).getColor(), Color.RED);
        assertEquals(board.getRoomsList(2).getColor(), Color.WHITE);
        assertEquals(board.getRoomsList(3).getColor(), Color.YELLOW);

        assertEquals(
                board.getRoomsList(1).getSquaresList(2).getAdjacent(Direction.EAST),
                board.getRoomsList().get(3).getSquaresList(0)
        );

        assertEquals(
                board.getRoomsList().get(3).getSquaresList().get(0).getAdjacent(Direction.WEST),
                board.getRoomsList().get(1).getSquaresList().get(2)
        );

        assertEquals(
                board.getRoomsList().get(2).getSquaresList().get(1).getAdjacent(Direction.NORTH),
                board.getRoomsList().get(1).getSquaresList().get(2)
        );

        assertEquals(
                board.getRoomsList().get(3).getSquaresList().get(0).getAdjacent(Direction.SOUTH),
                board.getRoomsList().get(3).getSquaresList().get(1)
        );

        assertEquals(
                board.getRoomsList().get(1).getSquaresList().get(2).getConnection(Direction.EAST),
                Connection.DOOR
        );

        assertEquals(
                board.getRoomsList().get(1).getSquaresList().get(1).getConnection(Direction.NORTH),
                Connection.WALL
        );

        assertEquals(
                board.getRoomsList().get(3).getSquaresList().get(0).getConnection(Direction.SOUTH),
                Connection.SQUARE
        );

        assertEquals(
                board.getRoomsList().get(2).getSquaresList().get(0).getConnection(Direction.WEST),
                Connection.ENDMAP
        );
    }
}