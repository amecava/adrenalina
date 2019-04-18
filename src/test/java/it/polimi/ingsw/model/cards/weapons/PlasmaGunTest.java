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

public class PlasmaGunTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void plasmaGun() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY, this.effectHandler);
        Player target1 = new Player("target1", Color.GREEN, this.effectHandler);

        AtomicTarget atomicTarget;

        source.movePlayer(board.getRoom(3).getSquare(1));
        target1.movePlayer(board.getRoom(0).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(3);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1));

        // Source can't see target1
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(1));

        // Move source distance not 1 or 2
        try {
            tester.useCard(EffectType.OPTIONAL_1,  atomicTarget);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertEquals(source.getCurrentPosition(), board.getRoom(3).getSquare(1));
        }

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(2));

        // Use optional 0
        try {
            tester.useCard(EffectType.OPTIONAL_1,  atomicTarget);

            assertEquals(source.getCurrentPosition(), board.getRoom(1).getSquare(2));
            assertEquals(source.getOldPosition(), board.getRoom(3).getSquare(1));
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(Arrays.asList(source));

        // Can't use effect on source
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            fail();
        } catch (EffectException e) {
            e.printStackTrace();
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget(Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, atomicTarget);

            assertSame(target1.getShots().get(0).getColor(), Color.GRAY);
            assertSame(target1.getShots().get(1).getColor(), Color.GRAY);
            assertSame(target1.getShots().size(), 2);
        } catch (EffectException | PropertiesException e) {
            fail();
        }

        atomicTarget = new AtomicTarget(board.getRoom(1).getSquare(1));

        // Optional effect already used
        try {
            tester.useCard(EffectType.OPTIONAL_1,  atomicTarget);
            fail();
        } catch (PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        atomicTarget = new AtomicTarget();

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL_2, atomicTarget);

            assertSame(target1.getShots().get(2).getColor(), Color.GRAY);
            assertSame(target1.getShots().size(), 3);
        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }
}