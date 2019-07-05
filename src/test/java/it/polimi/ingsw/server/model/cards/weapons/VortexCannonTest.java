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
 * Tests VortexCannon, with a test method for every effect it has.
 */
public class VortexCannonTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void primaryEffect() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(1).getSquare(1));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(3).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(8);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(1), Arrays.asList(target1));

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        // Vortex same square as source current position
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(board.getRoom(3).getSquare(1), Arrays.asList(target1));

        // Source can't see vortex
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(2), Arrays.asList(target2));

        // Target not 0 or 1 move away from vortex
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(2), Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());

            assertSame(target1.getShots().get(0), Color.GRIGIO);
            assertSame(target1.getShots().get(1), Color.GRIGIO);

            assertTrue(board.getRoom(1).getSquare(2).getPlayers().contains(target1));
            assertEquals(target1.getCurrentPosition(), board.getRoom(1).getSquare(2));
            assertEquals(target1.getOldPosition(), board.getRoom(0).getSquare(2));

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
        Player target3 = new Player("target2", Color.ROSSO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(1).getSquare(1));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(3).getSquare(0));
        target3.movePlayer(board.getRoom(3).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(8);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(2), Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());

            assertSame(target1.getShots().get(0), Color.GRIGIO);
            assertSame(target1.getShots().get(1), Color.GRIGIO);

            assertTrue(board.getRoom(1).getSquare(2).getPlayers().contains(target1));
            assertEquals(target1.getCurrentPosition(), board.getRoom(1).getSquare(2));
            assertEquals(target1.getOldPosition(), board.getRoom(0).getSquare(2));

        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(2), Arrays.asList(target1));

        // Can't use optional on same target as primary
        try {
            tester.useCard(EffectType.OPTIONAL_1, effectArgument, new ArrayList<>());

            fail();
        } catch (PropertiesException | CardException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1, target2));

        // Can't use optional on same target as primary
        try {
            tester.useCard(EffectType.OPTIONAL_1, effectArgument, new ArrayList<>());

            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target2, target3));

        // Use optional 0
        try {
            tester.useCard(EffectType.OPTIONAL_1, effectArgument, new ArrayList<>());

            assertSame(target2.getShots().get(0), Color.GRIGIO);
            assertSame(target3.getShots().get(0), Color.GRIGIO);

            assertTrue(board.getRoom(1).getSquare(2).getPlayers().contains(target2));
            assertTrue(board.getRoom(1).getSquare(2).getPlayers().contains(target3));
            assertEquals(target2.getCurrentPosition(), board.getRoom(1).getSquare(2));
            assertEquals(target3.getCurrentPosition(), board.getRoom(1).getSquare(2));

            assertEquals(target2.getOldPosition(), board.getRoom(3).getSquare(0));
            assertEquals(target3.getOldPosition(), board.getRoom(3).getSquare(0));

        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }
    }
}
