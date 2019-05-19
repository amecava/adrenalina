package it.polimi.ingsw.server.model.points;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PointHandlerTest {

    EffectHandler effectHandler = new EffectHandler();

    @Test
    void deathUpdate() {

        Player player1 = new Player("jacopo", Color.BLUE);
        Player player2 = new Player("Amedeo", Color.GREEN);
        Player player3 = new Player("federico", Color.YELLOW);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        PointHandler pointHandler = new PointHandler(playerList, 4);
        for (int i = 0; i < 5; i++) {
            player1.markPlayer(player2.getColor());
            player1.damagePlayer(player3.getColor(), true);
        }
        player1.damagePlayer(player2.getColor(), true);
        player1.damagePlayer(player2.getColor(), true);
        player1.damagePlayer(player2.getColor(), true);
        player1.damagePlayer(player2.getColor(), true);
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();
            if (pointHandler.checkEndGame()) {

                throw new EndGameException(pointHandler.endGame());
            }
        } catch (EndGameException e) {

            assertEquals(e.getWinner().get(0).get(0), player2);
        }

        assertEquals(player2.getMarks().size(), 1);
        assertEquals(
                player2.getMarks().get(0),
                player1.getColor()
        );
        assertEquals(player2.getPoints(), 8);
        assertEquals(player3.getPoints(), 7);
    }

    @Test
    void multipleDeaths() {

        Player player1 = new Player("jacopo", Color.VIOLET);
        Player player2 = new Player("Amedeo", Color.GREEN);
        Player player3 = new Player("federico", Color.YELLOW);
        Player player4 = new Player("Giulia ", Color.LIGHTBLUE);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        PointHandler pointHandler = new PointHandler(playerList, 4);

        for (int i = 0; i < 5; i++) {

            player1.markPlayer(player2.getColor());
            player1.markPlayer(player3.getColor());
        }

        for (int i = 0; i < 4; i++) {

            player1.damagePlayer(player2.getColor(), true);
        }

        player1.damagePlayer(player4.getColor(), true);
        player1.damagePlayer(player3.getColor(), true);
        player1.damagePlayer(player3.getColor(), true);

        player1.markPlayer(Color.LIGHTBLUE);
        player1.markPlayer(Color.LIGHTBLUE);

        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();

            if (pointHandler.checkEndGame()) {

                throw new EndGameException(pointHandler.endGame());
            }
        } catch (EndGameException e) {

            fail();
        }

        for (int i = 0; i < 6; i++) {

            player1.damagePlayer(player2.getColor(), true);
            player1.damagePlayer(player3.getColor(), true);
        }
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();
            if (pointHandler.checkEndGame()) {

                throw new EndGameException(pointHandler.endGame());
            }

        }  catch (EndGameException e) {
            e.printStackTrace();
        }
        assertEquals(player2.getPoints(), 16);
        assertEquals(player3.getPoints(), 10);
        assertEquals(player3.getMarks().size(), 2);
        assertEquals(player4.getPoints(), 4);
    }

    @Test
    void getWinner() {

        Player player1 = new Player("jacopo", Color.VIOLET);
        Player player2 = new Player("Amedeo", Color.GREEN);
        Player player3 = new Player("federico", Color.YELLOW);
        Player player4 = new Player("Giulia ", Color.LIGHTBLUE);
        Player player5 = new Player("Martina ", Color.RED);
        Player player6 = new Player("Veronica ", Color.BLUE);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        playerList.add(player5);
        playerList.add(player6);
        PointHandler pointHandler = new PointHandler(playerList, 2);
        player1.setPoints(20);
        player2.setPoints(20);
        player3.setPoints(15);

        /*
        assertTrue(pointHandler.getWinner().get(0).contains(player1));
        assertTrue(pointHandler.getWinner().get(0).contains(player2));
        assertFalse(pointHandler.getWinner().get(0).contains(player3));

         */

        for (int i = 0; i < 13; i++) {
            player2.damagePlayer(player1.getColor(), true);
        }
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();

            if (pointHandler.checkEndGame()) {

                throw new EndGameException(pointHandler.endGame());
            }
        } catch (EndGameException e) {
            fail();
        }
        for (int i = 0; i < 12; i++) {
            player1.damagePlayer(player2.getColor(), true);
        }
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();

            if (pointHandler.checkEndGame()) {

                throw new EndGameException(pointHandler.endGame());
            }

            fail();
        } catch (EndGameException e) {

            assertTrue(e.getWinner().get(0).contains(player1));
            /*
            assertFalse(e.getWinner().get(0).contains(player1));
            assertFalse(e.getWinner().get(0).contains(player1));

             */
        }
    }


    @Test
    void multipleKills() {
        Player player1 = new Player("jacopo", Color.VIOLET);
        Player player2 = new Player("Amedeo", Color.GREEN);
        Player player3 = new Player("federico", Color.YELLOW);
        Player player4 = new Player("Giulia ", Color.LIGHTBLUE);
        Player player5 = new Player("Martina ", Color.RED);
        Player player6 = new Player("Veronica ", Color.BLUE);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        playerList.add(player5);
        playerList.add(player6);
        PointHandler pointHandler = new PointHandler(playerList, 3);
        for (int i = 1; i <= 12; i++) {
            player2.damagePlayer(player1.getColor(), true);
            player3.damagePlayer(player1.getColor(), true);
        }
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();

            if (pointHandler.checkEndGame()) {

                throw new EndGameException(pointHandler.endGame());
            }
        } catch (EndGameException e) {
            fail();
        }
        player1.damagePlayer(player2.getColor(), true);
        player1.damagePlayer(player3.getColor(), true);
        assertTrue(player1.getShots().size() == 4);
        for (int i = 0; i < 11; i++) {
            player5.damagePlayer(player4.getColor(), true);
            player6.damagePlayer(player4.getColor(), true);
        }
        player1.damagePlayer(player4.getColor(), true);
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();

            if (pointHandler.checkEndGame()) {

                throw new EndGameException(pointHandler.endGame());
            }

        } catch (EndGameException e) {

            assertEquals(9, player2.getPoints());
            assertEquals(6, player3.getPoints());
            assertEquals(26, player1.getPoints());
            assertEquals(28, player4.getPoints());

            assertTrue(e.getWinner().get(0).contains(player4)
                    && e.getWinner().get(0).size() == 1);

            assertTrue(e.getWinner().get(2).contains(player2)
                    && e.getWinner().get(2).size() == 1);

            assertTrue(e.getWinner().get(3).contains(player3)
                    && e.getWinner().get(3).size() == 1);

            assertTrue(e.getWinner().get(1).contains(player1)
                    && e.getWinner().get(1).size() == 1);

            assertEquals(5, player1.getShots().size());

            /*
            assertEquals(6, pointHandler.getDeaths().getShots().size());
             */
        }

    }


}
