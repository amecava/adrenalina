package it.polimi.ingsw.model.points;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PointHandlerTest {

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
            player1.markPlayer(player2.getPlayerColor(), 1);
            player1.damagePlayer(player3.getPlayerColor(), 1);
        }
        player1.damagePlayer(player2.getPlayerColor(), 4);
        pointHandler.checkIfDead();

        assertEquals(player2.getMarks().size(), 1);
        assertEquals(
                player2.getMarks().get(0).getColor(),
                player1.getPlayerColor()
        );
        assertEquals(player2.getPoints(), 8);
        assertEquals(player3.getPoints(), 7);
        assertEquals(pointHandler.getWinner().get(0), player2);
    }

    @Test
    void multipleDeaths() {

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
        PointHandler pointHandler = new PointHandler(playerList, 4);
        for (int i = 0; i < 5; i++) {
            player1.markPlayer(player2.getPlayerColor(), 1);
            player1.markPlayer(player3.getPlayerColor(), 1);
        }
        player1.damagePlayer(player2.getPlayerColor(), 4);
        player1.damagePlayer(player4.getPlayerColor(), 1);
        player1.damagePlayer(player3.getPlayerColor(), 2);
        player1.markPlayer(Color.LIGHTBLUE, 2);
        pointHandler.checkIfDead();
        player1.damagePlayer(player2.getPlayerColor(), 6);
        player1.damagePlayer(player3.getPlayerColor(), 6);
        pointHandler.checkIfDead();
        for (Player player: playerList){
            System.out.println("" + player + player.getPoints());
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
        PointHandler pointHandler = new PointHandler(playerList, 4);
        player1.setPoints(20);
        player2.setPoints(20);
        player3.setPoints(15);
        assertTrue(pointHandler.getWinner().contains(player1));
        assertTrue(pointHandler.getWinner().contains(player2));
        assertFalse(pointHandler.getWinner().contains(player3));

        player2.damagePlayer(player1.getPlayerColor(), 13);
        pointHandler.checkIfDead();
        player1.damagePlayer(player2.getPlayerColor(),12);
        pointHandler.checkIfDead();

        assertTrue(pointHandler.getWinner().contains(player1));
        assertFalse(pointHandler.getWinner().contains(player2));
        assertFalse(pointHandler.getWinner().contains(player3));
    }
}
