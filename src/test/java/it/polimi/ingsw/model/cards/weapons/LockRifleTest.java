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
    private WeaponDeck weaponDeck = new WeaponDeck.WeaponDeckBuilder(this.effectHandler).build();

    @Test
    void primaryEffect() {

        Board board = new Board.BoardBuilder(0).build();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);

        AtomicTarget atomicTarget;

        board.getRoom(0).getSquare(0).addPlayer(source);
        board.getRoom(1).getSquare(2).addPlayer(target1);
        board.getRoom(3).getSquare(1).addPlayer(target2);

        WeaponCard tester = weaponDeck.getCard(0);
        tester.setOwner(source);

        effectHandler.setActivePlayer(target1);

        try {
            tester.useCard();
            fail();
        } catch (CardException e) {
            assertTrue(true);
        }

        effectHandler.setActivePlayer(source);

        atomicTarget = new AtomicTarget(Arrays.asList(target1, target2));

        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget();

        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target2));

        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1));

        try {
            tester.usePrimary(atomicTarget);

            assertSame(target1.getBridge().getShots().get(0).getColor(), Color.GRAY);
            assertSame(target1.getBridge().getShots().get(1).getColor(), Color.GRAY);
            assertSame(target1.getBridge().getMarks().get(0).getColor(), Color.GRAY);

        } catch (EffectException | PropertiesException e) {
            fail();
        }

    }

    @Test
    void optionalEffect() {

        Board board = new Board.BoardBuilder(0).build();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);

        AtomicTarget atomicTarget;

        board.getRoom(0).getSquare(0).addPlayer(source);
        board.getRoom(1).getSquare(2).addPlayer(target1);
        board.getRoom(0).getSquare(2).addPlayer(target2);

        WeaponCard tester = weaponDeck.getCard(0);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        atomicTarget = new AtomicTarget(Arrays.asList(target2));

        try {
            tester.useCard();

            tester.useOptional(0, atomicTarget);
            fail();
        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1));

        try {
            tester.usePrimary(atomicTarget);

            assertSame(target1.getBridge().getShots().get(0).getColor(), Color.GRAY);
            assertSame(target1.getBridge().getShots().get(1).getColor(), Color.GRAY);
            assertSame(target1.getBridge().getMarks().get(0).getColor(), Color.GRAY);

        } catch (EffectException | PropertiesException e) {
            fail();
        }

        try {
            tester.useOptional(0, atomicTarget);
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target2));

        try {
            tester.useOptional(0, atomicTarget);

            assertSame(target1.getBridge().getMarks().get(0).getColor(), Color.GRAY);

        } catch (CardException | EffectException | PropertiesException e) {
            fail();
        }
    }
}