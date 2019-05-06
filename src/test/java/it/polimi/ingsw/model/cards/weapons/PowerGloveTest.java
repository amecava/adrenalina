package it.polimi.ingsw.model.cards.weapons;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.ammo.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.decks.WeaponDeck;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.CostException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class PowerGloveTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void primaryEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);
        Player target3 = new Player("target3", Color.BLUE);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(0));
        target2.movePlayer(board.getRoom(0).getSquare(1));
        target3.movePlayer(board.getRoom(0).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(19);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
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

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Target must be exactly one move away
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target3));

        // Target must be exactly one move away
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target2));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());

            assertEquals(target2.getShots().size(), 1);
            assertEquals(target2.getMarks().size(), 2);

            assertEquals(target2.getCurrentPosition(), board.getRoom(0).getSquare(1));

            assertEquals(source.getCurrentPosition(), board.getRoom(0).getSquare(1));
            assertEquals(source.getOldPosition(), board.getRoom(0).getSquare(0));
        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Card not loaded
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (PropertiesException | CardException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }
    }

    @Test
    void alternativeEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);
        Player target2 = new Player("target2", Color.LIGHTBLUE);
        Player target3 = new Player("target3", Color.BLUE);
        Player target4 = new Player("target4", Color.RED);
        Player target5 = new Player("target5", Color.YELLOW);
        Player target6 = new Player("target6", Color.WHITE);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(2));

        target1.movePlayer(board.getRoom(1).getSquare(2));
        target2.movePlayer(board.getRoom(2).getSquare(1));

        target3.movePlayer(board.getRoom(0).getSquare(1));
        target4.movePlayer(board.getRoom(0).getSquare(1));

        target5.movePlayer(board.getRoom(3).getSquare(0));

        target6.movePlayer(board.getRoom(0).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(19);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target2));

        // Target2 is through wall
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target3, target4));

        // Targets on same square
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target5));

        // Targets not cardinal
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target3, target6));

        // Use alternative
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());

            assertEquals(target3.getShots().size(), 2);
            assertEquals(target6.getShots().size(), 2);

            assertEquals(source.getCurrentPosition(), board.getRoom(0).getSquare(0));
            assertEquals(target3.getCurrentPosition(), board.getRoom(0).getSquare(1));
            assertEquals(target6.getCurrentPosition(), board.getRoom(0).getSquare(0));
        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Card not loaded
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (PropertiesException | CardException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        tester.getOwner().getAmmoCubesList().forEach(x -> x.setUsed(false));

        try {

            tester.reloadWeapon(new ArrayList<>());

            assertTrue(tester.isLoaded());
        } catch (CostException e) {
            fail();
        }

        source.movePlayer(board.getRoom(0).getSquare(2));

        effectArgument = new EffectArgument(Arrays.asList(target6, target3));

        // Use alternative wrong order
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());

            assertEquals(target3.getShots().size(), 4);
            assertEquals(target6.getShots().size(), 4);

            assertEquals(source.getCurrentPosition(), board.getRoom(0).getSquare(0));
        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }
    }
}