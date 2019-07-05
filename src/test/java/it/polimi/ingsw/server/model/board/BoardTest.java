package it.polimi.ingsw.server.model.board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.exceptions.cards.SquareException;
import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.board.rooms.Connection;
import it.polimi.ingsw.server.model.board.rooms.Direction;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import org.junit.jupiter.api.Test;

/**
 * Tests the Board methods.
 */
class BoardTest {

    /**
     * Tests that the boards are built correctly.
     */
    @Test
    void buildBoard() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);

        assertEquals(board.getRoom(0).getColor(), Color.BLU);
        assertEquals(board.getRoom(1).getColor(), Color.ROSSO);
        assertEquals(board.getRoom(2).getColor(), Color.BIANCO);
        assertEquals(board.getRoom(3).getColor(), Color.GIALLO);

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

    /**
     * Tests the board json creation.
     */
    @Test
    void squareJson() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);
        board.fillBoard();
    }

    /**
     * Tests the filling of the boards with new WeaponCards and new AmmoTiles.
     */
    @Test
    void fillBoard() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);
        board.fillBoard();

        board.getRoomsList().stream()
                .flatMap(x -> x.getSquaresList().stream())
                .forEach(y -> {

                    if (y.isSpawn()) {

                        assertEquals(y.getTools().size(), 3);

                    } else {

                        assertEquals(y.getTools().size(), 1);
                    }
                });
    }

    /**
     * Tests the method that finds the spawn square given a selected color.
     */
    @Test
    void findSpawn() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);

        try {

            assertEquals(board.findSpawn(Color.ROSSO).getRoom().getColor(), Color.ROSSO);

        } catch (SquareException e) {

            fail();
        }

        try {

            board.findSpawn(Color.VIOLA);

            fail();

        } catch (SquareException e) {

            assertTrue(true);
        }
    }

    /**
     * Tests the method that finds a square in the board given the room color and the squareId.
     */
    @Test
    void findSquare() {

        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);

        try {

            Square square = board.findSquare("rosso", "0");

            assertEquals(square.getRoom().getColor(), Color.ROSSO);

        } catch (SquareException | ColorException e) {

            fail();
        }

        try {

            Square square = board.findSquare("ROSSO", "0");

            assertEquals(square.getRoom().getColor(), Color.ROSSO);

        } catch (SquareException | ColorException e) {

            fail();
        }

        try {

            Square square = board.findSquare("roso", "0");

            assertEquals(square.getRoom().getColor(), Color.ROSSO);

        } catch (SquareException | ColorException e) {

            fail();
        }

        try {

            Square square = board.findSquare("dfgdfg", "0");

            fail();

        } catch (SquareException e) {

            fail();

        } catch (ColorException e) {

            assertTrue(true);
        }

        try {

            Square square = board.findSquare("rosso", "5");

            fail();

        } catch (SquareException e) {

            assertTrue(true);

        } catch (ColorException e) {

            fail();
        }
    }
}