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

public class PlasmaGunTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void plasmaGun() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRAY);
        Player target1 = new Player("target1", Color.GREEN);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(3).getSquare(1));
        target1.movePlayer(board.getRoom(0).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(4);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Source can't see target1
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(1));

        // Move source distance not 1 or 2
        try {
            tester.useCard(EffectType.OPTIONAL_1, effectArgument, new ArrayList<>());
            fail();
        } catch (EffectException | CardException e) {
            fail();
        } catch (PropertiesException e) {
            assertEquals(source.getCurrentPosition(), board.getRoom(3).getSquare(1));
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(2));

        // Use optional 0
        try {
            tester.useCard(EffectType.OPTIONAL_1, effectArgument, new ArrayList<>());

            assertEquals(source.getCurrentPosition(), board.getRoom(1).getSquare(2));
            assertEquals(source.getOldPosition(), board.getRoom(3).getSquare(1));
        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(source));

        // Can't use effect on source
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());

            fail();
        } catch (EffectException | CardException e) {
            e.printStackTrace();
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Use primary
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());

            assertSame(target1.getShots().get(0), Color.GRAY);
            assertSame(target1.getShots().get(1), Color.GRAY);
            assertSame(target1.getShots().size(), 2);
        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }

        effectArgument = new EffectArgument(board.getRoom(1).getSquare(1));

        // Optional effect already used
        try {
            tester.useCard(EffectType.OPTIONAL_1, effectArgument, new ArrayList<>());
            fail();
        } catch (PropertiesException | CardException e) {
            fail();
        } catch (EffectException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument();

        // Use optional 1
        try {
            tester.useCard(EffectType.OPTIONAL_2, effectArgument, new ArrayList<>());

            assertSame(target1.getShots().get(2), Color.GRAY);
            assertSame(target1.getShots().size(), 3);
        } catch (EffectException | PropertiesException | CardException e) {
            fail();
        }
    }
}