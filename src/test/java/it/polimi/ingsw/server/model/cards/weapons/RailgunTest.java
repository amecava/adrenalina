package it.polimi.ingsw.server.model.cards.weapons;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.ammo.AmmoCube;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.exceptions.cards.CostException;
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
import java.util.List;
import org.junit.jupiter.api.Test;

class RailgunTest {

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

        source.movePlayer(board.getRoom(0).getSquare(2));
        target1.movePlayer(board.getRoom(2).getSquare(0));
        target2.movePlayer(board.getRoom(2).getSquare(1));

        target3.movePlayer(board.getRoom(0).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(15);
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

        // Not cardinal
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target3));

        // Ok
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertEquals(target3.getShots().size(), 3);
        } catch (CardException | EffectException | PropertiesException e) {
            fail();
        }

        List<AmmoCube> before = source.getAmmoCubesList();

        try {
            tester.reloadWeapon(new ArrayList<>());
        } catch (CostException e) {

            assertEquals(before.stream().filter(x -> !x.isUsed()).count(),
                    source.getAmmoCubesList().stream().filter(x -> !x.isUsed()).count());
        }

        source.getAmmoCubesList().forEach(x -> x.setUsed(false));

        try {
            tester.reloadWeapon(new ArrayList<>());
            assertEquals(source.getAmmoCubesList().stream().filter(AmmoCube::isUsed).count(),
                    tester.getReloadCost().size());
        } catch (CostException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target2));

        // Ok
        try {
            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertEquals(target2.getShots().size(), 3);
        } catch (CardException | EffectException | PropertiesException e) {
            fail();
        }

        source.getAmmoCubesList().forEach(x -> x.setUsed(false));

        try {
            tester.reloadWeapon(Arrays.asList(board.getPowerUpDeck().getDeck().stream()
                    .filter(x -> x.getColor().equals(Color.BLUE)).findAny().get()));
            assertEquals(source.getAmmoCubesList().stream().filter(AmmoCube::isUsed).count(),
                    tester.getReloadCost().size() - 1);
            assertEquals(source.getAmmoCubesList().stream()
                    .filter(x -> x.getColor().equals(Color.BLUE) && x.isUsed()).count(), 0);
        } catch (CostException e) {
            fail();
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

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(2));
        target1.movePlayer(board.getRoom(1).getSquare(2));

        target2.movePlayer(board.getRoom(2).getSquare(1));
        target3.movePlayer(board.getRoom(1).getSquare(2));

        WeaponCard tester = weaponDeck.getCard(15);
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

        effectArgument = new EffectArgument(Arrays.asList(target1, target2, target3));

        // Too many targets
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            fail();
        } catch (CardException | EffectException e) {
            fail();
        } catch (PropertiesException e) {
            assertTrue(true);
        }

        effectArgument = new EffectArgument(Arrays.asList(target2, target3));

        // Ok
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            assertEquals(target2.getShots().size(), 2);
            assertEquals(target3.getShots().size(), 2);
        } catch (CardException | EffectException | PropertiesException e) {
            e.printStackTrace();
            fail();
        }

        List<AmmoCube> before = source.getAmmoCubesList();

        try {
            tester.reloadWeapon(new ArrayList<>());
        } catch (CostException e) {

            assertEquals(before.stream().filter(x -> !x.isUsed()).count(),
                    source.getAmmoCubesList().stream().filter(x -> !x.isUsed()).count());
        }

        source.getAmmoCubesList().forEach(x -> x.setUsed(false));

        try {
            tester.reloadWeapon(new ArrayList<>());
            assertEquals(source.getAmmoCubesList().stream().filter(AmmoCube::isUsed).count(),
                    tester.getReloadCost().size());
        } catch (CostException e) {
            fail();
        }

        effectArgument = new EffectArgument(Arrays.asList(target2));

        // Ok
        try {
            tester.useCard(EffectType.ALTERNATIVE, effectArgument, new ArrayList<>());
            assertEquals(target2.getShots().size(), 4);
        } catch (CardException | EffectException | PropertiesException e) {
            fail();
        }
    }
}
