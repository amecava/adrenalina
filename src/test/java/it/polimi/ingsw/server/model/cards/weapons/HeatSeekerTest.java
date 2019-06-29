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

class HeatSeekerTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void primaryEffect1() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);
        Player target3 = new Player("target3", Color.BLU);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(1));
        target2.movePlayer(board.getRoom(0).getSquare(0));
        target3.movePlayer(board.getRoom(3).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(10);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(2), Arrays.asList(target1, target2));

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

            fail();

        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {

            assertTrue(true);
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(2), Arrays.asList(board.getRoom(1).getSquare(2)));

        // Wrong target type
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | PropertiesException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(1).getSquare(2)));

        // Wrong target type
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        } catch (EffectException | CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target2, target3));

        // Too many targets
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target2));

        // Can see target
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target3));

        // Can see target
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertTrue(true);
        } catch (CardException | EffectException | PropertiesException e) {
            fail();
        }
    }

    @Test
    void primaryEffect2() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target = new Player("target1", Color.GRIGIO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target.movePlayer(board.getRoom(2).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(10);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target));

        // Can see target
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertTrue(true);
        } catch (CardException | EffectException | PropertiesException e) {
            fail();
        }
    }
}