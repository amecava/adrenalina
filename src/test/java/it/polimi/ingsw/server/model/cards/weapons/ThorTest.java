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
 * Tests Thor, with a test method for every effect it has.
 */
class ThorTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void thor() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.AZZURRO);
        Player target3 = new Player("target3", Color.BLU);
        Player target4 = new Player("target4", Color.ROSSO);
        Player target5 = new Player("target5", Color.GIALLO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(1));
        target1.movePlayer(board.getRoom(0).getSquare(2));
        target2.movePlayer(board.getRoom(1).getSquare(1));
        target3.movePlayer(board.getRoom(2).getSquare(1));
        target4.movePlayer(board.getRoom(2).getSquare(0));
        target5.movePlayer(board.getRoom(3).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(3);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target4));

        // Source can't see target4
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target4));

        // Optional not activated
        try {
            tester.useCard(EffectType.OPTIONAL_2, effectArgument, new ArrayList<>());
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

            assertEquals(target1.getShots().size(), 2);
        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target4));

        // Optional not activated
        try {
            tester.useCard(EffectType.OPTIONAL_2, effectArgument, new ArrayList<>());
            fail();
        } catch (PropertiesException | CardException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target3));

        // Seen by active violated
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

            assertEquals(target2.getShots().size(), 1);
        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target5));

        tester.getOwner().getAmmoCubesList().forEach(x -> x.setUsed(false));

        // Seen by active violated
        try {
            tester.useCard(EffectType.OPTIONAL_2, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target3));

        // Use optional 2
        try {
            tester.useCard(EffectType.OPTIONAL_2, effectArgument, new ArrayList<>());

            assertEquals(target3.getShots().size(), 2);
        } catch (EffectException | PropertiesException | CardException e) {
            e.printStackTrace();
            fail();
        }
    }
}