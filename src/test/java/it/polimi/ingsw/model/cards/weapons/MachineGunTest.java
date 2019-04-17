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

class MachineGunTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void machineGun1() {

        Board board = new Board.BoardBuilder(0).build(effectHandler);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);
        Player target3 = new Player("target3", Color.BLUE);
        Player target4 = new Player("target4", Color.RED);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(1).getSquare(0));
        target3.movePlayer(board.getRoom(1).getSquare(2));
        target4.movePlayer(board.getRoom(1).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(1);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1, target2));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertEquals(target1.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target3, target4));

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target2, target3));

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);

            assertEquals(target3.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 2);
        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }

    @Test
    void machineGun2() {

        Board board = new Board.BoardBuilder(0).build(effectHandler);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);
        Player target3 = new Player("target3", Color.BLUE);
        Player target4 = new Player("target4", Color.RED);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(1).getSquare(0));
        target3.movePlayer(board.getRoom(1).getSquare(2));
        target4.movePlayer(board.getRoom(1).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(1);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1, target2));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertEquals(target1.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target3, target4));

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target3, target2));

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);

            assertEquals(target3.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 2);
        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }

    @Test
    void machineGun3() {

        Board board = new Board.BoardBuilder(0).build(effectHandler);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);
        Player target3 = new Player("target3", Color.BLUE);
        Player target4 = new Player("target4", Color.RED);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(1).getSquare(0));
        target3.movePlayer(board.getRoom(1).getSquare(2));
        target4.movePlayer(board.getRoom(1).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(1);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1, target2));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertEquals(target1.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target3, target4));

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target2));

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);

            assertEquals(target3.getShots().size(), 0);
            assertEquals(target2.getShots().size(), 2);
        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }

    @Test
    void machineGun4() {

        Board board = new Board.BoardBuilder(0).build(effectHandler);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);
        Player target3 = new Player("target3", Color.BLUE);
        Player target4 = new Player("target4", Color.RED);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(1).getSquare(0));
        target3.movePlayer(board.getRoom(1).getSquare(2));
        target4.movePlayer(board.getRoom(1).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(1);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1, target2));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertEquals(target1.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1));

        // Use optional 0
        try {
            tester.useCard(EffectType.OPTIONAL1,  atomicTarget);

            assertEquals(target1.getShots().size(), 2);
            assertEquals(target2.getShots().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1, target3));

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target2, target3));

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);

            assertEquals(target1.getShots().size(), 2);
            assertEquals(target2.getShots().size(), 2);
            assertEquals(target3.getShots().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }
}