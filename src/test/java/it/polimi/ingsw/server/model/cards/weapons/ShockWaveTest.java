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
 * Tests ShockWave, with a test method for every effect it has.
 */
class ShockWaveTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void primaryEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);
        Player target3 = new Player("target3", Color.BLU);
        Player target4 = new Player("target4", Color.GIALLO);
        Player target5 = new Player("target5", Color.ROSSO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(1).getSquare(1));
        target1.movePlayer(board.getRoom(1).getSquare(0));
        target2.movePlayer(board.getRoom(1).getSquare(2));
        target3.movePlayer(board.getRoom(2).getSquare(0));

        target4.movePlayer(board.getRoom(1).getSquare(0));
        target5.movePlayer(board.getRoom(3).getSquare(0));



        WeaponCard tester = weaponDeck.getCard(20);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = null;

        // Null effectArgument
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target4, target3));

        // Same square
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {

            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target2, target5));

        // Not max dist 1
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {

            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target2, target3));

        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertEquals(source.getShots().size(), 0);
            assertEquals(target3.getShots().size(), 1);
            assertEquals(target1.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);
        } catch (CardException | PropertiesException  | EffectException e) {
            fail();
        }

        // Card not loaded
        try {
            tester.activateCard();
            fail();
        } catch (CardException e) {
            assertTrue(true);
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
        Player target4 = new Player("target4", Color.GIALLO);
        Player target5 = new Player("target5", Color.ROSSO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(1).getSquare(1));
        target1.movePlayer(board.getRoom(1).getSquare(0));
        target2.movePlayer(board.getRoom(1).getSquare(2));
        target3.movePlayer(board.getRoom(2).getSquare(0));
        target4.movePlayer(board.getRoom(1).getSquare(0));
        target5.movePlayer(board.getRoom(2).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(20);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(2), Arrays.asList(target1));

        // Wrong number of arguments
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Wrong number of arguments
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument();

        // Ok
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            assertEquals(source.getShots().size(), 0);
            assertEquals(target3.getShots().size(), 1);
            assertEquals(target1.getShots().size(), 1);
            assertEquals(target4.getShots().size(), 1);
            assertEquals(target5.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);
        } catch (CardException | PropertiesException  | EffectException e) {
            e.printStackTrace();
            fail();
        }
    }
}