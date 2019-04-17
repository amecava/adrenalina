package it.polimi.ingsw.model.cards.weapons;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicTarget;
import it.polimi.ingsw.model.decks.WeaponDeck;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class LockRifleTest {

    private EffectHandler effectHandler = new EffectHandler();


    @Test
    void primaryEffect() {

        Board board = new Board.BoardBuilder(0).build(effectHandler);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(1).getSquare(2));
        target2.movePlayer(board.getRoom(3).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(0);
        tester.setOwner(source);

        effectHandler.setActivePlayer(target1);

        // Not the turn of the card owner
        try {
            tester.activateCard();
            fail();
        } catch (CardException e) {
            assertTrue(true);
        }

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1, target2));

        // Alternative effect not present
        try {
            tester.useAlternative(atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1, target2));

        // Too many targets
        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(board.getRoom(0).getSquare(0)));

        // Wrong target type
        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget();

        // Wrong method call
        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target2));

        // Source can't see target2
        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1));

        // Use primary
        try {
            tester.usePrimary(atomicTarget);

            assertSame(target1.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target1.getShots().get(1).getColor(), Color.GRAY);
            assertSame(target1.getShots().size(), 2);

            assertSame(target1.getMarks().get(0).getColor(), Color.GRAY);
            assertSame(target1.getMarks().size(), 1);

        } catch (EffectException | PropertiesException e) {
            fail();
        }

    }

    @Test
    void optionalEffect() {

        Board board = new Board.BoardBuilder(0).build(effectHandler);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(1).getSquare(2));
        target2.movePlayer(board.getRoom(0).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(0);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        atomicTarget = new AtomicTarget(Arrays.asList(target2));

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        // Optional not activated
        try {
            tester.useOptional(0, atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1));

        // Use primary
        try {
            tester.usePrimary(atomicTarget);

            assertSame(target1.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target1.getShots().get(1).getColor(), Color.GRAY);
            assertSame(target1.getMarks().get(0).getColor(), Color.GRAY);

        } catch (EffectException | PropertiesException e) {
            fail();
        }

        // Same as father violated
        try {
            tester.useOptional(0, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target2));

        // Use optional 0
        try {
            tester.useOptional(0, atomicTarget);

            assertSame(target1.getMarks().get(0).getColor(), Color.GRAY);

        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }
}