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

public class VortexCannonTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void primaryEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY, this.effectHandler);
        Player target1 = new Player("target1", Color.GREEN, this.effectHandler);
        Player target2 = new Player("target2", Color.LIGHTBLUE, this.effectHandler);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(1).getSquare(1));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(3).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(7);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(1), Arrays.asList(target1));

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        // Vortex same square as source current position
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(board.getRoom(3).getSquare(1), Arrays.asList(target1));

        // Source can't see vortex
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(2), Arrays.asList(target2));

        // Target not 0 or 1 move away from vortex
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(2), Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertSame(target1.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target1.getShots().get(1).getColor(), Color.GRAY);

            assertTrue(board.getRoom(1).getSquare(2).getPlayers().contains(target1));
            assertEquals(target1.getCurrentPosition(), board.getRoom(1).getSquare(2));
            assertEquals(target1.getOldPosition(), board.getRoom(0).getSquare(2));

        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }

    @Test
    void optionalEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY, this.effectHandler);
        Player target1 = new Player("target1", Color.GREEN, this.effectHandler);
        Player target2 = new Player("target2", Color.LIGHTBLUE, this.effectHandler);
        Player target3 = new Player("target2", Color.RED, this.effectHandler);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(1).getSquare(1));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(3).getSquare(0));
        target3.movePlayer(board.getRoom(3).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(7);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(2), Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertSame(target1.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target1.getShots().get(1).getColor(), Color.GRAY);

            assertTrue(board.getRoom(1).getSquare(2).getPlayers().contains(target1));
            assertEquals(target1.getCurrentPosition(), board.getRoom(1).getSquare(2));
            assertEquals(target1.getOldPosition(), board.getRoom(0).getSquare(2));

        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(2), Arrays.asList(target1));

        // Can't use optional on same target as primary
        try {
            tester.useCard(EffectType.OPTIONAL1, atomicTarget);

            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1, target2));

        // Can't use optional on same target as primary
        try {
            tester.useCard(EffectType.OPTIONAL1, atomicTarget);

            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target2, target3));

        // Use optional 0
        try {
            tester.useCard(EffectType.OPTIONAL1, atomicTarget);

            assertSame(target2.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target3.getShots().get(0).getColor(), Color.GRAY);

            assertTrue(board.getRoom(1).getSquare(2).getPlayers().contains(target2));
            assertTrue(board.getRoom(1).getSquare(2).getPlayers().contains(target3));
            assertEquals(target2.getCurrentPosition(), board.getRoom(1).getSquare(2));
            assertEquals(target3.getCurrentPosition(), board.getRoom(1).getSquare(2));

            assertEquals(target2.getOldPosition(), board.getRoom(3).getSquare(0));
            assertEquals(target3.getOldPosition(), board.getRoom(3).getSquare(0));

        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }
}
