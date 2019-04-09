package it.polimi.ingsw.model.board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Connection;
import it.polimi.ingsw.model.exceptions.FileException;
import org.junit.jupiter.api.Test;

class BoardBuilderTest {

    @Test
    void buildBoard() {
        BoardBuilder boardBuilder = new BoardBuilder();
        Board board = new Board();

        try {

            board = boardBuilder.buildBoard(0);
        } catch (FileException e){
            e.printStackTrace();
        }

        assertTrue(board.getRoomsList().get(0).getColor() == Color.BLUE);
        assertTrue(board.getRoomsList().get(1).getColor() == Color.RED);
        assertTrue(board.getRoomsList().get(2).getColor() == Color.WHITE);
        assertTrue(board.getRoomsList().get(3).getColor() == Color.YELLOW);
        assertTrue(board.getRoomsList().get(1).getSquaresList().get(2).getEast() == board
                .getRoomsList().get(3).getSquaresList().get(0));
        assertTrue(board.getRoomsList().get(3).getSquaresList().get(0).getWest() == board
                .getRoomsList().get(1).getSquaresList().get(2));
        assertTrue(board.getRoomsList().get(2).getSquaresList().get(1).getNorth() == board
                .getRoomsList().get(1).getSquaresList().get(2));
        assertTrue(board.getRoomsList().get(3).getSquaresList().get(0).getSouth() == board
                .getRoomsList().get(3).getSquaresList().get(1));
        assertTrue(board.getRoomsList().get(1).getSquaresList().get(2).getEastConnection()
                == Connection.DOOR);
        assertTrue(board.getRoomsList().get(1).getSquaresList().get(1).getNorthConnection()
                == Connection.WALL);
        assertTrue(board.getRoomsList().get(3).getSquaresList().get(0).getSouthConnection()
                == Connection.SQUARE);
        assertTrue(board.getRoomsList().get(2).getSquaresList().get(0).getWestConnection()
                == Connection.ENDMAP);
    }
}