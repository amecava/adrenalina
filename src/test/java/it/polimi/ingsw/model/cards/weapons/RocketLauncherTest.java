package it.polimi.ingsw.model.cards.weapons;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicTarget;
import it.polimi.ingsw.model.decks.WeaponDeck;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class RocketLauncherTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void rocketLauncher1() {

        Board board = new Board.BoardBuilder(0).build(effectHandler);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);
        Player target3 = new Player("target2", Color.RED);
        Player target4 = new Player("target2", Color.BLUE);
        Player target5 = new Player("target2", Color.YELLOW);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(0));
        target2.movePlayer(board.getRoom(0).getSquare(1));
        target3.movePlayer(board.getRoom(0).getSquare(1));
        target4.movePlayer(board.getRoom(0).getSquare(2));
        target5.movePlayer(board.getRoom(0).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(13);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1));

        // Wrong method call
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(board.getRoom(0).getSquare(1), Arrays.asList(target1));

        // Target on same square of source
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        target1.movePlayer(board.getRoom(0).getSquare(1));

        atomicTarget = new AtomicTarget(board.getRoom(0).getSquare(1), Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertEquals(target1.getShots().size(), 2);
            assertEquals(target1.getCurrentPosition(), board.getRoom(0).getSquare(1));
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget();

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);

            assertEquals(target1.getShots().size(), 3);
            assertEquals(target2.getShots().size(), 1);
            assertEquals(target3.getShots().size(), 1);
            assertEquals(target4.getShots().size(), 0);
            assertEquals(target5.getShots().size(), 0);

            assertEquals(target1.getCurrentPosition(), board.getRoom(0).getSquare(1));
        } catch (EffectException | PropertiesException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void rocketLauncher2() {

        Board board = new Board.BoardBuilder(0).build(effectHandler);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);
        Player target3 = new Player("target2", Color.RED);
        Player target4 = new Player("target2", Color.BLUE);
        Player target5 = new Player("target2", Color.YELLOW);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(0).getSquare(1));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(0).getSquare(2));
        target3.movePlayer(board.getRoom(0).getSquare(2));
        target4.movePlayer(board.getRoom(1).getSquare(2));
        target5.movePlayer(board.getRoom(1).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(13);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(2), Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertEquals(target1.getShots().size(), 2);
            assertEquals(target1.getCurrentPosition(), board.getRoom(1).getSquare(2));
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget();

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);

            assertEquals(target1.getShots().size(), 3);
            assertEquals(target2.getShots().size(), 1);
            assertEquals(target3.getShots().size(), 1);
            assertEquals(target4.getShots().size(), 0);
            assertEquals(target5.getShots().size(), 0);

            assertEquals(target1.getCurrentPosition(), board.getRoom(1).getSquare(2));
        } catch (EffectException | PropertiesException e) {
            e.printStackTrace();
            fail();
        }
    }
}