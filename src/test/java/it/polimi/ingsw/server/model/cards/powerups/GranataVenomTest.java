package it.polimi.ingsw.server.model.cards.powerups;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.decks.WeaponDeck;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.cards.CostException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class GranataVenomTest {

    private EffectHandler effectHandler = new EffectHandler();

    @Test
    void granataVenom1() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(1);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        // Ok
        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        target1.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("GRANATAVENOM")).findAny().get());

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Damage
        try {

            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertEquals(target1.getShots().size(), 2);

        } catch (CardException | PropertiesException | EffectException e) {
            fail();
        }

        effectArgument = new EffectArgument();

        // Ok
        try {

            target1.getPowerUpsList().get(0).useCard(effectArgument);
            assertEquals(target1.getShots().size(), 2);
            assertEquals(source.getMarks().size(), 1);
            assertFalse(this.effectHandler.getActive().contains(source));

        } catch (CardException | PropertiesException | EffectException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void granataVenom2() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(1));

        WeaponCard tester = weaponDeck.getCard(2);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        // Ok
        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        target1.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("GRANATAVENOM")).findAny().get());

        effectArgument = new EffectArgument(Arrays.asList(target1));

        // Damage
        try {

            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertEquals(target1.getShots().size(), 1);

        } catch (CardException | PropertiesException | EffectException e) {
            fail();
        }

        // Test synchronized effect handler
        for (int i = 0; i < 5; i++) {

            source.getAmmoCubesList().forEach(x -> x.setUsed(false));

            try {
                tester.reloadWeapon(new ArrayList<>());
                assertTrue(true);
            } catch (CostException e) {

                fail();
            }

            Thread damage = new Thread(() -> {

                // Damage
                try {

                    tester.useCard(EffectType.PRIMARY, new EffectArgument(Arrays.asList(target1)), new ArrayList<>());

                } catch (CardException | PropertiesException | EffectException e) {
                    e.printStackTrace();
                    fail();
                }
            });

            Thread granata = new Thread(() -> {

                try {

                    target1.getPowerUpsList().get(0).useCard(new EffectArgument());

                } catch (CardException | PropertiesException | EffectException e) {
                    e.printStackTrace();
                    fail();
                }
            });

            damage.start();
            granata.start();

            try {
                damage.join();
                granata.join();
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }

        assertEquals(target1.getShots().size(), 6);
        assertEquals(source.getMarks().size(), 3);

    }

    @Test
    void granataVenom3() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        WeaponDeck weaponDeck = board.getWeaponDeck();

        Player source = new Player("source", Color.GRIGIO);
        Player target1 = new Player("target1", Color.VERDE);
        Player target2 = new Player("target2", Color.ROSSO);

        EffectArgument effectArgument;

        source.movePlayer(board.getRoom(0).getSquare(0));
        target1.movePlayer(board.getRoom(0).getSquare(0));
        target2.movePlayer(board.getRoom(0).getSquare(0));

        WeaponCard tester = weaponDeck.getCard(6);
        tester.setOwner(source);

        effectHandler.setActivePlayer(source);

        // Ok
        try {
            tester.activateCard();
            assertTrue(true);
        } catch (CardException e) {
            fail();
        }

        target1.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("GRANATAVENOM")).findAny().get());
        board.getPowerUpDeck().getDeck().remove(target1.getPowerUpsList().get(0));
        target2.addPowerUp(board.getPowerUpDeck().getDeck().stream().filter(x -> x.getName().equals("GRANATAVENOM")).findAny().get());

        effectArgument = new EffectArgument();

        // Damage
        try {

            tester.useCard(EffectType.PRIMARY, effectArgument, new ArrayList<>());
            assertEquals(target1.getShots().size(), 1);
            assertEquals(target2.getShots().size(), 1);

        } catch (CardException | PropertiesException | EffectException e) {
            fail();
        }

        // Test synchronized effect handler
        for (int i = 0; i < 5; i++) {

            source.getAmmoCubesList().forEach(x -> x.setUsed(false));

            try {
                tester.reloadWeapon(new ArrayList<>());
                assertTrue(true);
            } catch (CostException e) {

                fail();
            }

            Thread damage = new Thread(() -> {

                // Damage
                try {

                    tester.useCard(EffectType.PRIMARY, new EffectArgument(), new ArrayList<>());

                } catch (CardException | PropertiesException | EffectException e) {
                    e.printStackTrace();
                    fail();
                }
            });

            Thread granata1 = new Thread(() -> {

                try {

                    target1.getPowerUpsList().get(0).useCard(new EffectArgument());

                } catch (CardException | PropertiesException | EffectException e) {
                    e.printStackTrace();
                    fail();
                }
            });

            Thread granata2 = new Thread(() -> {

                try {

                    target2.getPowerUpsList().get(0).useCard(new EffectArgument());

                } catch (CardException | PropertiesException | EffectException e) {
                    e.printStackTrace();
                    fail();
                }
            });

            damage.start();
            granata1.start();
            granata2.start();

            try {
                damage.join();
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }

        assertEquals(target1.getShots().size(), 6);
        assertEquals(target2.getShots().size(), 6);
        assertEquals(source.getMarks().size(), 6);

    }
}
