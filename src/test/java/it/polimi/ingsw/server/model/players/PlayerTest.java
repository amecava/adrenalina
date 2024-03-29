package it.polimi.ingsw.server.model.players;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.ammo.AmmoTile;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.server.model.exceptions.cards.FullHandException;
import it.polimi.ingsw.server.model.exceptions.cards.SquareException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * Tests the Player class.
 */
class PlayerTest {

    EffectHandler effectHandler = new EffectHandler();

    /**
     * Tests the move player action/
     */
    @Test
    void movePlayer() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);

        Player player = new Player("player", Color.GRIGIO);

        player.movePlayer(board.getRoom(0).getSquare(0));

        assertEquals(player.getCurrentPosition(), board.getRoom(0).getSquare(0));

        player.movePlayer(board.getRoom(1).getSquare(1));

        assertEquals(player.getCurrentPosition(), board.getRoom(1).getSquare(1));
        assertEquals(player.getOldPosition(), board.getRoom(0).getSquare(0));

        assertTrue(board.getRoom(1).getSquare(1).getPlayers().contains(player));
        assertFalse(board.getRoom(0).getSquare(0).getPlayers().contains(player));
    }

    /**
     * Tests the collect from not spawn method.
     */
    @Test
    void collectFromNotSpawn() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        board.fillBoard();

        Card tester;

        Player player = new Player("player", Color.GRIGIO);

        player.movePlayer(board.getRoom(1).getSquare(2));

        try {

            tester = player.collect();

            //tests collect method first
            assertEquals(3, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.GIALLO).count());
            assertEquals(1, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.BLU).count());
            assertEquals(2, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.ROSSO).count());
            assertEquals(Arrays.asList(Color.GIALLO, Color.ROSSO, Color.GIALLO),
                    ((AmmoTile) tester).getAmmoCubesList());

        } catch (SquareException | EmptySquareException e) {
            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

        player.movePlayer(board.getRoom(0).getSquare(1));

        try {

            tester = player.collect();

            //tests that the player keeps his activated ammo cubes after moving and earns new ones
            assertEquals(3, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.GIALLO).count());
            assertEquals(2, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.BLU).count());
            assertEquals(2, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.ROSSO).count());
            assertEquals(Arrays.asList(Color.GIALLO, Color.BLU),
                    ((AmmoTile) tester).getAmmoCubesList());

        } catch (SquareException | EmptySquareException e) {
            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

        player.movePlayer(board.getRoom(0).getSquare(0));

        try {

            tester = player.collect();

            //tests that if every cube is activated everything works fine
            assertEquals(3, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.GIALLO).count());
            assertEquals(2, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.BLU).count());
            assertEquals(3, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.ROSSO).count());
            assertEquals(Arrays.asList(Color.GIALLO, Color.ROSSO),
                    ((AmmoTile) tester).getAmmoCubesList());

        } catch (SquareException | EmptySquareException e) {
            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

        //tests that if you already collected everything in the square the right exception gets thrown
        player.movePlayer(board.getRoom(0).getSquare(0));

        try {

            player.collect();
            fail();
        } catch (EmptySquareException e) {

            assertTrue(true);
        } catch (SquareException e) {

            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

        //tests the correct behaviour of the method if the player is in a spawn square
        player.movePlayer(board.getRoom(0).getSquare(2));

        try {

            player.collect();
            fail();
        } catch (SquareException e) {

            assertTrue(true);
        } catch (EmptySquareException e) {
            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }


    }

    /**
     * Tests the collect from spawn method.
     */
    @Test
    void collectFromSpawn() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        board.fillBoard();

        Player player = new Player("player", Color.GRIGIO);

        player.movePlayer(board.getRoom(0).getSquare(2));

        //selected card not in square
        try {

            player.movePlayer(board.getRoom(0).getSquare(2));
            player.collect(15, new ArrayList<PowerUpCard>());

            fail();

        } catch (FullHandException | SquareException e) {

            fail();
        } catch (EmptySquareException e) {

            assertTrue(true);
        } catch (CardException e) {
            e.printStackTrace();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

        // wrong method call from square
        try {

            player.movePlayer(board.getRoom(0).getSquare(1));
            player.collect(1, new ArrayList<PowerUpCard>());

            fail();

        } catch (FullHandException | EmptySquareException e) {

            fail();
        } catch (SquareException e) {

            assertTrue(true);
        } catch (CardException e) {
            e.printStackTrace();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

        // hand is full
        try {

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.addWeaponCard(board.getWeaponDeck().getCard());
            player.addWeaponCard(board.getWeaponDeck().getCard());
            player.addWeaponCard(board.getWeaponDeck().getCard());

            player.collect(2, new ArrayList<PowerUpCard>());

            fail();

        } catch (SquareException | EmptySquareException e) {

            fail();
        } catch (FullHandException e) {

            assertTrue(true);
        } catch (CardException e) {
            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

        // card added to hand
        try {

            player.getWeaponCardList().clear();

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.collect(1, new ArrayList<PowerUpCard>());

            assertEquals("LOCK RIFLE", player.getWeaponCardList().get(0).getName());
        } catch (SquareException | FullHandException | EmptySquareException e) {
            fail();
        } catch (CardException e) {
            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

        // empty square, no more weapon cards
        try {

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.collect(2, new ArrayList<PowerUpCard>());

            assertEquals("MACHINE GUN", player.getWeaponCardList().get(1).getName());

            player.collect(3, new ArrayList<PowerUpCard>());

            assertEquals("T.H.O.R.", player.getWeaponCardList().get(2).getName());

            player.getWeaponCardList().clear();

            player.collect(4, new ArrayList<PowerUpCard>());

            fail();

        } catch (SquareException | FullHandException e) {

            fail();
        } catch (EmptySquareException e) {

            assertTrue(true);
        } catch (CardException e) {
            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

    }

    /**
     * Tests the collect from spawn method.
     */
    @Test
    void collectFromSpawn2() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        board.fillBoard();

        Player player = new Player("player", Color.GRIGIO);

        try {

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.addWeaponCard(board.getWeaponDeck().getCard());
            player.addWeaponCard(board.getWeaponDeck().getCard());
            player.addWeaponCard(board.getWeaponDeck().getCard());

            player.collect(15, 2, new ArrayList<PowerUpCard>());

            fail();

        } catch (SquareException | EmptySquareException e) {

            fail();

        } catch (CardException e) {
            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }

        try {

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.addWeaponCard(board.getWeaponDeck().getCard());
            player.addWeaponCard(board.getWeaponDeck().getCard());
            player.addWeaponCard(board.getWeaponDeck().getCard());

            player.collect(15, 3, new ArrayList<PowerUpCard>());

            assertTrue(player.getWeaponCardList().stream().map(x -> (WeaponCard) x)
                    .anyMatch(y -> y.getId() == 3));
            assertTrue(player.getCurrentPosition().getTools().stream().map(x -> (WeaponCard) x)
                    .anyMatch(y -> y.getId() == 15));

        } catch (SquareException | EmptySquareException e) {

            fail();

        } catch (CardException e) {
            fail();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }
    }
}