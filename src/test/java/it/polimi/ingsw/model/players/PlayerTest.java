package it.polimi.ingsw.model.players;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.ammo.AmmoCube;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.cards.CardNotFoundException;
import it.polimi.ingsw.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.model.exceptions.cards.FullHandException;
import it.polimi.ingsw.model.exceptions.cards.SquareTypeException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class PlayerTest {

    EffectHandler effectHandler = new EffectHandler();

    @Test
    void movePlayer() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);

        Player player = new Player("player", Color.GRAY, this.effectHandler);

        player.movePlayer(board.getRoom(0).getSquare(0));

        assertEquals(player.getCurrentPosition(), board.getRoom(0).getSquare(0));

        player.movePlayer(board.getRoom(1).getSquare(1));

        assertEquals(player.getCurrentPosition(), board.getRoom(1).getSquare(1));
        assertEquals(player.getOldPosition(), board.getRoom(0).getSquare(0));

        assertTrue(board.getRoom(1).getSquare(1).getPlayers().contains(player));
        assertFalse(board.getRoom(0).getSquare(0).getPlayers().contains(player));
    }

    @Test
    void collectFromNotSpawn() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        board.fill();

        Card tester;

        Player player = new Player("player", Color.GRAY, this.effectHandler);

        player.movePlayer(board.getRoom(1).getSquare(2));

        try {

            tester = player.collect();

            //tests collect method first
            assertEquals(3, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.YELLOW).count());
            assertEquals(1, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.BLUE).count());
            assertEquals(2, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.RED).count());
            assertEquals(Arrays.asList(Color.YELLOW, Color.RED, Color.YELLOW),
                    ((AmmoTile) tester).getAmmoCubesList());

        } catch (SquareTypeException | EmptySquareException e) {
            fail();
        }

        player.movePlayer(board.getRoom(0).getSquare(1));

        try {

            tester = player.collect();

            //tests that the player keeps his activated ammo cubes after moving and earns new ones
            assertEquals(3, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.YELLOW).count());
            assertEquals(2, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.BLUE).count());
            assertEquals(2, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.RED).count());
            assertEquals(Arrays.asList(Color.YELLOW, Color.BLUE),
                    ((AmmoTile) tester).getAmmoCubesList());

        } catch (SquareTypeException | EmptySquareException e) {
            fail();
        }

        player.movePlayer(board.getRoom(0).getSquare(0));

        try {

            tester = player.collect();

            //tests that if every cube is activated everything works fine
            assertEquals(3, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.YELLOW).count());
            assertEquals(2, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.BLUE).count());
            assertEquals(3, player.getAmmoCubesList().stream().filter(x -> !x.isUsed())
                    .filter(y -> y.getColor() == Color.RED).count());
            assertEquals(Arrays.asList(Color.YELLOW, Color.RED),
                    ((AmmoTile) tester).getAmmoCubesList());

        } catch (SquareTypeException | EmptySquareException e) {
            fail();
        }

        //tests that if you already collected everything in the square the right exception gets thrown
        player.movePlayer(board.getRoom(0).getSquare(0));

        try {

            player.collect();
            fail();
        } catch (EmptySquareException e) {

            assertTrue(true);
        } catch (SquareTypeException e) {

            fail();
        }

        //tests the correct behaviour of the method if the player is in a spawn square
        player.movePlayer(board.getRoom(0).getSquare(2));

        try {

            player.collect();
            fail();
        } catch (SquareTypeException e) {

            assertTrue(true);
        } catch (EmptySquareException e) {
            fail();
        }


    }

    @Test
    void collectFromSpawn() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        board.fill();

        Player player = new Player("player", Color.GRAY, this.effectHandler);

        player.movePlayer(board.getRoom(0).getSquare(2));

        //selected card not in square
        try {

            player.movePlayer(board.getRoom(0).getSquare(2));
            player.collect(15);

            fail();

        } catch (FullHandException | SquareTypeException e) {

            fail();
        } catch (EmptySquareException e) {

            assertTrue(true);
        }

        // wrong method call from square
        try {

            player.movePlayer(board.getRoom(0).getSquare(1));
            player.collect(1);

            fail();

        } catch (FullHandException | EmptySquareException e) {

            fail();
        } catch (SquareTypeException e) {

            assertTrue(true);
        }

        // hand is full
        try {

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.setWeaponHand(board.getWeaponDeck().getCardsForSpawnSquares());

            player.collect(2);

            fail();

        } catch (SquareTypeException | EmptySquareException e) {

            fail();
        } catch (FullHandException e) {

            assertTrue(true);
        }

        // card added to hand
        try {

            player.clearHand();

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.collect(1);

            assertEquals("LOCK RIFLE", player.getWeaponHand().get(0).getName());
        } catch (SquareTypeException | FullHandException | EmptySquareException e) {
            fail();
        }

        // empty square, no more weapon cards
        try {

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.collect(2);

            assertEquals("MACHINE GUN", player.getWeaponHand().get(1).getName());

            player.collect(3);

            assertEquals("T.H.O.R.", player.getWeaponHand().get(2).getName());

            player.clearHand();

            player.collect(4);

            fail();

        } catch (SquareTypeException | FullHandException e) {

            fail();
        } catch (EmptySquareException e) {

            assertTrue(true);
        }

    }

    @Test
    void collectFromSpawn2() {

        Board board = new Board.BoardBuilder(this.effectHandler).build(0);
        board.fill();

        Player player = new Player("player", Color.GRAY, this.effectHandler);


        try {

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.setWeaponHand(board.getWeaponDeck().getCardsForSpawnSquares());

            player.collect(15, 2);

            fail();

        } catch (SquareTypeException | EmptySquareException e) {

            fail();

        } catch (CardNotFoundException e) {

            assertTrue(true);
        }


        try {

            player.movePlayer(board.getRoom(0).getSquare(2));

            player.setWeaponHand(board.getWeaponDeck().getCardsForSpawnSquares());

            player.collect(15, 3);

            assertTrue(player.getWeaponHand().stream().map(x -> (WeaponCard)x).anyMatch(y -> y.getId() == 3));
            assertTrue(player.getCurrentPosition().getTools().stream().map(x -> (WeaponCard)x).anyMatch(y -> y.getId() == 15));

        } catch (SquareTypeException | EmptySquareException e) {

            fail();

        } catch (CardNotFoundException e) {

            fail();
        }

    }
}