package it.polimi.ingsw.server.model.cards.weapons;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.decks.WeaponDeck;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * Tests LockRifle, with a test method for every effect it has.
 */
class LockRifleTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void primaryEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(1).getSquare(2));
        target2.movePlayer(board.getRoom(3).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(1);
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

        effectArgument = new EffectArgument(Arrays.asList(target1, target2));

        // Alternative effect not present
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (PropertiesException | CardException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target2));

        // Too many targets
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(0).getSquare(0)));

        // Wrong target type
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument();

        // Wrong method call
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (PropertiesException | CardException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target2));

        // Source can't see target2
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());

            assertSame(target1.getShots().get(0), Color.GRIGIO);
            assertSame(target1.getShots().get(1), Color.GRIGIO);
            assertSame(target1.getShots().size(), 2);

            assertSame(target1.getMarks().get(0), Color.GRIGIO);
            assertSame(target1.getMarks().size(), 1);

        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }

    }

    @Test
    void optionalEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(1).getSquare(2));
        target2.movePlayer(board.getRoom(0).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(1);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        effectArgument = new EffectArgument(Arrays.asList(target2));

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        // Optional not activated
        try {
            tester.useCard(EffectType.OPTIONAL_1, effectArgument, new ArrayList<>());
            fail();
        } catch (PropertiesException | CardException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());

            assertSame(target1.getShots().get(0), Color.GRIGIO);
            assertSame(target1.getShots().get(1), Color.GRIGIO);
            assertSame(target1.getMarks().get(0), Color.GRIGIO);

        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }

        // Same as father violated
        try {
            tester.useCard(EffectType.OPTIONAL_1, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target2));

        // Use optional 0
        try {
            tester.useCard(EffectType.OPTIONAL_1, effectArgument, new ArrayList<>());

            assertSame(target1.getMarks().get(0), Color.GRIGIO);

        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }
    }
}