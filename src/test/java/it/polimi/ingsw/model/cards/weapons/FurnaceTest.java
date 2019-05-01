package it.polimi.ingsw.model.cards.weapons;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.decks.WeaponDeck;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class FurnaceTest {

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
        target1.movePlayer(board.getRoom(1).getSquare(1));
        target2.movePlayer(board.getRoom(1).getSquare(2));
        target3.movePlayer(board.getRoom(1).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(8);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(1).getSquare(2)));

        // Wrong target type
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(0)));

        // Same room as source
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(3)));

        // Source can't see room
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(1), board.getRoom(1)));

        // Duplicates found
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(1)));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument);

            assertEquals(target1.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);
            assertEquals(target3.getShots().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }

    void alternativeEffect() {

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
        target3.movePlayer(board.getRoom(0).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(8);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(0).getSquare(0)));

        // Distance property violated
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(0).getSquare(1),
                board.getRoom(0).getSquare(1)));

        // Duplicate exception
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument);
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(board.getRoom(0).getSquare(1)));

        // Use alternative
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument);

            assertEquals(target2.getShots().size(), 1);
            assertEquals(target3.getShots().size(), 1);
            assertEquals(target2.getMarks().size(), 1);
            assertEquals(target3.getMarks().size(), 1);
        } catch (EffectException | PropertiesException e) {
            fail();
        }
    }
}