package it.polimi.ingsw.model.cards.effects.properties;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.exceptions.board.SquareException;
import org.junit.jupiter.api.Test;

class ViewInspectorTest {

    Board board = new Board.BoardBuilder(0).build();

    int distOne;
    boolean cardinal = true;
    boolean throughWalls = true;
    ViewInspector viewInspector = new ViewInspector();

    Square one = board.getRoomsList(2).getSquaresList(1);
    Square two = board.getRoomsList(0).getSquaresList(2);

    Square three = board.getRoomsList(2).getSquaresList(0);
    Square four = board.getRoomsList(0).getSquaresList(1);

    Square five = board.getRoomsList(0).getSquaresList(0);
    Square six = board.getRoomsList(3).getSquaresList(1);

    Square seven = board.getRoomsList(1).getSquaresList(1);

    @Test
    void computeDistance() {

        try {

            distOne = viewInspector.computeDistance(one, two, cardinal, throughWalls);
            assertEquals(2, distOne);

        } catch (SquareException e) {
            fail();
        }

        try {

            distOne = viewInspector.computeDistance(three, four, cardinal, !throughWalls);

        } catch (SquareException e) {
            assertTrue(true);
        }

        try {

            distOne = viewInspector.computeDistance(three, four, cardinal, throughWalls);
            assertEquals(2, distOne);
        } catch (SquareException e) {
            fail();
        }

        try {

            distOne = viewInspector.computeDistance(five, six, !cardinal, !throughWalls);
            assertEquals(5, distOne);

        } catch (SquareException e) {
            fail();
        }

        try {

            distOne = viewInspector.computeDistance(five, five, !cardinal, !throughWalls);
            assertEquals(0, distOne);

        } catch (SquareException e) {
            fail();
        }

        try {

            distOne = viewInspector.computeDistance(five, two, !cardinal, !throughWalls);
            assertEquals(2, distOne);

        } catch (SquareException e) {
            fail();
        }

        try {

            distOne = viewInspector.computeDistance(four, seven, !cardinal, !throughWalls);
            assertEquals(3, distOne);

        } catch (SquareException e) {
            fail();
        }

        try {

            distOne = viewInspector.computeDistance(four, seven, !cardinal, throughWalls);
            assertEquals(1, distOne);

        } catch (SquareException e) {
            fail();
        }

        try {

            distOne = viewInspector.computeDistance(one, two, !cardinal, !throughWalls);
            assertEquals(4, distOne);

        } catch (SquareException e) {
            fail();
        }

        try {

            distOne = viewInspector.computeDistance(one, four, !cardinal, !throughWalls);
            assertEquals(5, distOne);

        } catch (SquareException e) {
            fail();
        }
    }


    @Test
    void targetView() {
        assertTrue(viewInspector.targetView(one, three));
        assertTrue(!viewInspector.targetView(one, two));
        assertTrue(viewInspector.targetView(five, seven));
        assertTrue(viewInspector.targetView(six, three));
        assertTrue(!viewInspector.targetView(four, seven));
    }

}