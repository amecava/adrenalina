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

class ThorTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void thor() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY, this.effectHandler);
        Player target1 = new Player("target1", Color.GREEN, this.effectHandler);
        Player target2 = new Player("target2", Color.LIGHTBLUE, this.effectHandler);
        Player target3 = new Player("target3", Color.BLUE, this.effectHandler);
        Player target4 = new Player("target4", Color.RED, this.effectHandler);
        Player target5 = new Player("target5", Color.YELLOW, this.effectHandler);

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
            tester.useCard(EffectType.PRIMARY, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target4));

        // Optional not activated
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertEquals(target1.getShots().size(), 2);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target4));

        // Optional not activated
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target3));

        // Seen by active violated
        try {
            tester.useCard(EffectType.OPTIONAL1, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target2));

        // Use optional 0
        try {
            tester.useCard(EffectType.OPTIONAL1, atomicTarget);

            assertEquals(target2.getShots().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target5));

        // Seen by active violated
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target3));

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL2, atomicTarget);

            assertEquals(target3.getShots().size(), 2);
        } catch (EffectException | PropertiesException e) {
            e.printStackTrace();
            fail();
        }
    }
}