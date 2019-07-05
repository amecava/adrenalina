package it.polimi.ingsw.server.model.cards.weapons;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.decks.WeaponDeck;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * Tests Sledgehammer, with a test method for every effect it has.
 */
class SledgehammerTest {

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
        target1.movePlayer(board.getRoom(0).getSquare(1));
        target2.movePlayer(board.getRoom(0).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(21);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(board.getRoom(0).getSquare(2));

        // Wrong args
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Not same square
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target2));

        // Ok
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertEquals(target2.getShots().size(), 2);
        } catch (CardException | EffectException | PropertiesException e) {
            fail();
        }
    }

    @Test
    void alternativeEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(2).getSquare(1));
        target1.movePlayer(board.getRoom(1).getSquare(2));
        target2.movePlayer(board.getRoom(2).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(21);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(board.getRoom(0).getSquare(2));

        // Wrong args
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(board.getRoom(0).getSquare(2), Arrays.asList(target1));

        // Not same square
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(2), Arrays.asList(target2));

        // Can't move through walls
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(board.getRoom(3).getSquare(1), Arrays.asList(target2));

        // Ok
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            assertEquals(target2.getShots().size(), 3);
            assertEquals(target2.getCurrentPosition(), board.getRoom(3).getSquare(1));
        } catch (CardException | EffectException | PropertiesException e) {
            e.printStackTrace();
            fail();
        }
    }
}