package it.polimi.ingsw.model.board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Connection;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import java.util.List;
import org.junit.jupiter.api.Test;

class BoardBuilderTest {

    @Test
    void buildBoard() {
        BoardBuilder boardBuilder = new BoardBuilder();
        Board board = new Board();

        board = boardBuilder.buildBoard(0);

        assertTrue(board.getRoomsList().get(0).getColor() == Color.BLUE);
        assertTrue(board.getRoomsList().get(1).getSquaresList().get(2).getEast() == board.getRoomsList().get(3).getSquaresList().get(0));
        assertTrue(board.getRoomsList().get(1).getSquaresList().get(2).getEastConnection() == Connection.DOOR);
    }
}