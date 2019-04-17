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

class ThorTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void thor() {

        Board board = new Board.BoardBuilder(0).build(effectHandler);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);
        Player target3 = new Player("target3", Color.BLUE);
        Player target4 = new Player("target4", Color.RED);
        Player target5 = new Player("target5", Color.YELLOW);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(0).getSquare(1));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(1).getSquare(1));
        target3.movePlayer(board.getRoom(2).getSquare(1));
        target4.movePlayer(board.getRoom(2).getSquare(0));
        target5.movePlayer(board.getRoom(3).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(2);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target4));

        // Source can't see target4
        try {
            tester.usePrimary(atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target4));

        // Optional not activated
        try {
            tester.useOptional(1, atomicTarget);
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

            assertEquals(target1.getShots().size(), 2);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target4));

        // Optional not activated
        try {
            tester.useOptional(1, atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target3));

        // Seen by active violated
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

            assertEquals(target2.getShots().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target5));

        // Seen by active violated
        try {
            tester.useOptional(1, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target3));

        // Use optional 1
        try {
            tester.useOptional(1, atomicTarget);

            assertEquals(target3.getShots().size(), 2);
        } catch (EffectException | PropertiesException e) {
            e.printStackTrace();
            fail();
        }
    }
}