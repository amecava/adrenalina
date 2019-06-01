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

class FlameThrowerTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void primaryEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(1);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);
        Player target3 = new Player("target3", Color.BLU);
        Player target4 = new Player("target4", Color.GIALLO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(2));
        target1.movePlayer(board.getRoom(4).getSquare(0));
        target2.movePlayer(board.getRoom(4).getSquare(2));

        target3.movePlayer(board.getRoom(4).getSquare(1));

        target4.movePlayer(board.getRoom(4).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(12);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target3));

        // Not cardinal with source
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target4));

        // Targets on same
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target2));

        // Ok
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertEquals(source.getShots().size(), 0);
            assertEquals(target1.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);
        } catch (CardException | PropertiesException  | EffectException e) {
            fail();
        }
    }

    @Test
    void alternativeEffect1() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);
        Player target3 = new Player("target3", Color.BLU);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(0));
        target2.movePlayer(board.getRoom(0).getSquare(0));
        target3.movePlayer(board.getRoom(1).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(6);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(1).getSquare(2)));

        // Wrong number of arguments
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument();

        // Wrong number of arguments
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertEquals(source.getShots().size(), 0);
            assertEquals(target3.getShots().size(), 0);
            assertEquals(target1.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);
        } catch (CardException | PropertiesException  | EffectException e) {
            fail();
        }

        // Card already used
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }
    }

    @Test
    void alternativeEffect2() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(1);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);
        Player target3 = new Player("target3", Color.BLU);
        Player target4 = new Player("target4", Color.GIALLO);
        Player target5 = new Player("target5", Color.GIALLO);
        Player target6 = new Player("target6", Color.GIALLO);
        Player target7 = new Player("target7", Color.GIALLO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(2));
        target1.movePlayer(board.getRoom(4).getSquare(0));
        target5.movePlayer(board.getRoom(4).getSquare(0));
        target2.movePlayer(board.getRoom(4).getSquare(2));
        target6.movePlayer(board.getRoom(4).getSquare(2));
        target7.movePlayer(board.getRoom(4).getSquare(2));

        target3.movePlayer(board.getRoom(4).getSquare(1));

        target4.movePlayer(board.getRoom(4).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(12);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        source.getAmmoCubesList().stream().forEach(x -> x.setUsed(false));

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(4).getSquare(0), board.getRoom(4).getSquare(1)));

        // Not cardinal with source
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(4).getSquare(0), board.getRoom(4).getSquare(0)));

        // Targets on same
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(4).getSquare(0), board.getRoom(4).getSquare(2)));

        // Ok
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());

            assertEquals(source.getShots().size(), 0);
            assertEquals(target1.getShots().size(), 2);
            assertEquals(target5.getShots().size(), 2);
            assertEquals(target2.getShots().size(), 1);
            assertEquals(target6.getShots().size(), 1);
            assertEquals(target7.getShots().size(), 1);
        } catch (CardException | PropertiesException  | EffectException e) {
            fail();
        }
    }
}