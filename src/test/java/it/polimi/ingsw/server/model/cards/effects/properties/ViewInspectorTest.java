package it.polimi.ingsw.server.model.cards.effects.properties;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.Target;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.exceptions.properties.SquareDistanceException;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the ViewInspector methods.
 */
class ViewInspectorTest {

    private EffectHandler effectHandler = new EffectHandler();
    Board board = new Board.BoardBuilder(this.effectHandler).build(0);

    private int distOne;
    private boolean cardinal = true;
    private boolean throughWalls = true;

    private Square one = board.getRoom(2).getSquare(1);
    private Square two = board.getRoom(0).getSquare(2);

    private Square three = board.getRoom(2).getSquare(0);
    private Square four = board.getRoom(0).getSquare(1);

    private Square five = board.getRoom(0).getSquare(0);
    private Square six = board.getRoom(3).getSquare(1);

    private Square seven = board.getRoom(1).getSquare(1);

    /**
     * Tests if square distances are well computed.
     */
    @Test
    void computeDistance() {

        try {

            distOne = ViewInspector.computeDistance(one, two, cardinal, throughWalls);
            assertEquals(2, distOne);

        } catch (SquareDistanceException e) {
            fail();
        }

        try {

            distOne = ViewInspector.computeDistance(three, four, cardinal, !throughWalls);

        } catch (SquareDistanceException e) {
            assertTrue(true);
        }

        try {

            distOne = ViewInspector.computeDistance(three, four, cardinal, throughWalls);
            assertEquals(2, distOne);
        } catch (SquareDistanceException e) {
            fail();
        }

        try {

            distOne = ViewInspector.computeDistance(five, six, !cardinal, !throughWalls);
            assertEquals(5, distOne);

        } catch (SquareDistanceException e) {
            fail();
        }

        try {

            distOne = ViewInspector.computeDistance(five, five, !cardinal, !throughWalls);
            assertEquals(0, distOne);

        } catch (SquareDistanceException e) {
            fail();
        }

        try {

            distOne = ViewInspector.computeDistance(five, two, !cardinal, !throughWalls);
            assertEquals(2, distOne);

        } catch (SquareDistanceException e) {
            fail();
        }

        try {

            distOne = ViewInspector.computeDistance(four, seven, !cardinal, !throughWalls);
            assertEquals(3, distOne);

        } catch (SquareDistanceException e) {
            fail();
        }

        try {

            distOne = ViewInspector.computeDistance(four, seven, !cardinal, throughWalls);
            assertEquals(1, distOne);

        } catch (SquareDistanceException e) {
            fail();
        }

        try {

            distOne = ViewInspector.computeDistance(one, two, !cardinal, !throughWalls);
            assertEquals(4, distOne);

        } catch (SquareDistanceException e) {
            fail();
        }

        try {

            distOne = ViewInspector.computeDistance(one, four, !cardinal, !throughWalls);
            assertEquals(5, distOne);

        } catch (SquareDistanceException e) {
            fail();
        }
    }


    /**
     * Tests if target view property is well computed.
     */
    @Test
    void targetView() {

        assertTrue(ViewInspector.targetView(one, three));
        assertTrue(!ViewInspector.targetView(one, two));
        assertTrue(ViewInspector.targetView(five, seven));
        assertTrue(ViewInspector.targetView(six, three));
        assertTrue(!ViewInspector.targetView(four, seven));
    }

    /**
     * Tests if the sameDirection property is well computed.
     */
    @Test
    void sameDirection() {

        List<Target> test = new ArrayList<>();
        test.add(two);
        test.add(four);

        assertTrue(ViewInspector.sameDirection(five, test));

        test.clear();
        test.add(six);
        test.add(one);

        assertTrue(ViewInspector.sameDirection(three, test));

        test.clear();
        test.add(four);
        Player testPlayer = new Player("test", Color.GRIGIO);
        testPlayer.movePlayer(six);
        test.add(testPlayer);

        assertTrue(!ViewInspector.sameDirection(seven, test));

        test.clear();
        test.add(two);
        testPlayer.movePlayer(four);
        test.add(testPlayer);

        assertTrue(ViewInspector.sameDirection(five, test));
    }

    /**
     * Tests the correct exception thrown during useCard and activateCard processes.
     */
    @Test
    void computeDistance2() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(1);

        Player source = new Player("source", Color.GRIGIO);
        Player target = new Player("target", Color.GIALLO);

        source.movePlayer(board.getRoom(3).getSquare(0));
        target.movePlayer(board.getRoom(0).getSquare(2));

        WeaponCard card = board.getWeaponDeck().getCard(15);

        card.setOwner(source);
        this.effectHandler.setActivePlayer(source);

        try {

            card.activateCard();

        } catch (CardException e) {

            fail();
        }

        EffectArgument effectArgument = new EffectArgument(Arrays.asList(target));

        try {

            card.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());

        } catch (EffectException | PropertiesException | CardException e) {

            fail();
        }
    }
}