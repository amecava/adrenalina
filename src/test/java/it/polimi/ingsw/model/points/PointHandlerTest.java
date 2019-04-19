package it.polimi.ingsw.model.points;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PointHandlerTest {

    EffectHandler effectHandler = new EffectHandler();

    @Test
    void deathUpdate() {

        Player player1 = new Player("jacopo", Color.BLUE, this.effectHandler);
        Player player2 = new Player("Amedeo", Color.GREEN, this.effectHandler);
        Player player3 = new Player("federico", Color.YELLOW, this.effectHandler);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        PointHandler pointHandler = new PointHandler(playerList, 4);
        for (int i = 0; i < 5; i++) {
            player1.markPlayer(player2.getPlayerColor());
            player1.damagePlayer(player3.getPlayerColor());
        }
        player1.damagePlayer(player2.getPlayerColor());
        player1.damagePlayer(player2.getPlayerColor());
        player1.damagePlayer(player2.getPlayerColor());
        player1.damagePlayer(player2.getPlayerColor());
        pointHandler.checkIfDead();

        assertEquals(player2.getMarks().size(), 1);
        assertEquals(
                player2.getMarks().get(0).getColor(),
                player1.getPlayerColor()
        );
        assertEquals(player2.getPoints(), 8);
        assertEquals(player3.getPoints(), 7);
        assertEquals(pointHandler.getWinner().get(0).get(0), player2);
    }

    @Test
    void multipleDeaths() {

        Player player1 = new Player("jacopo", Color.VIOLET, this.effectHandler);
        Player player2 = new Player("Amedeo", Color.GREEN, this.effectHandler);
        Player player3 = new Player("federico", Color.YELLOW, this.effectHandler);
        Player player4 = new Player("Giulia ", Color.LIGHTBLUE, this.effectHandler);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        PointHandler pointHandler = new PointHandler(playerList, 4);
        for (int i = 0; i < 5; i++) {
            player1.markPlayer(player2.getPlayerColor());
            player1.markPlayer(player3.getPlayerColor());
        }
        for (int i = 0; i < 4; i++) {
            player1.damagePlayer(player2.getPlayerColor());
        }
        player1.damagePlayer(player4.getPlayerColor());
        player1.damagePlayer(player3.getPlayerColor());
        player1.damagePlayer(player3.getPlayerColor());
        player1.markPlayer(Color.LIGHTBLUE);
        player1.markPlayer(Color.LIGHTBLUE);
        pointHandler.checkIfDead();
        for (int i = 0; i < 6; i++) {
            player1.damagePlayer(player2.getPlayerColor());
            player1.damagePlayer(player3.getPlayerColor());
        }
        pointHandler.checkIfDead();
        assertEquals(player2.getPoints(), 16);
        assertEquals(player3.getPoints(), 10);
        assertEquals(player3.getMarks().size(), 2);
        assertEquals(player4.getPoints(), 4);
    }

    @Test
    void getWinner() {
        Player player1 = new Player("jacopo", Color.VIOLET, this.effectHandler);
        Player player2 = new Player("Amedeo", Color.GREEN, this.effectHandler);
        Player player3 = new Player("federico", Color.YELLOW, this.effectHandler);
        Player player4 = new Player("Giulia ", Color.LIGHTBLUE, this.effectHandler);
        Player player5 = new Player("Martina ", Color.RED, this.effectHandler);
        Player player6 = new Player("Veronica ", Color.BLUE, this.effectHandler);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        playerList.add(player5);
        playerList.add(player6);
        PointHandler pointHandler = new PointHandler(playerList, 4);
        player1.setPoints(20);
        player2.setPoints(20);
        player3.setPoints(15);
        assertTrue(pointHandler.getWinner().get(0).contains(player1));
        assertTrue(pointHandler.getWinner().get(0).contains(player2));
        assertFalse(pointHandler.getWinner().get(0).contains(player3));

        for (int i = 0; i < 13; i++) {
            player2.damagePlayer(player1.getPlayerColor());
        }
        pointHandler.checkIfDead();
        for (int i = 0; i < 12; i++) {
            player1.damagePlayer(player2.getPlayerColor());
        }
        pointHandler.checkIfDead();
        int k = 1;
        for (List<Player> playerList1 : pointHandler.getWinner()) {
            System.out.println(" player in " + k + "position ");
            for (Player player : playerList1) {
                System.out.println(player.getPlayerId());
            }
            k++;
        }
        assertTrue(pointHandler.getWinner().get(0).contains(player1));
        assertFalse(pointHandler.getWinner().get(0).contains(player2));
        assertFalse(pointHandler.getWinner().get(0).contains(player3));
    }


    @Test
    void multipleKills (){
        Player player1 = new Player("jacopo", Color.VIOLET, this.effectHandler);
        Player player2 = new Player("Amedeo", Color.GREEN, this.effectHandler);
        Player player3 = new Player("federico", Color.YELLOW, this.effectHandler);
        Player player4 = new Player("Giulia ", Color.LIGHTBLUE, this.effectHandler);
        Player player5 = new Player("Martina ", Color.RED, this.effectHandler);
        Player player6 = new Player("Veronica ", Color.BLUE, this.effectHandler);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        playerList.add(player5);
        playerList.add(player6);
        PointHandler pointHandler = new PointHandler(playerList, 3);
        for (int i=1; i<=12; i++){
            player2.damagePlayer(player1.getPlayerColor());
            player3.damagePlayer(player1.getPlayerColor());
        }
        pointHandler.checkIfDead();
        player1.damagePlayer(player2.getPlayerColor());
        player1.damagePlayer(player3.getPlayerColor());
        assertTrue(player1.getShots().size()==4);
        for (int i=0; i<11; i++){
            player5.damagePlayer(player4.getPlayerColor());
            player6.damagePlayer(player4.getPlayerColor());
        }
        player1.damagePlayer(player4.getPlayerColor());
        pointHandler.checkIfDead();
        assertEquals(9, player2.getPoints());
        assertEquals(6, player3.getPoints());
        assertEquals(26, player1.getPoints());
        assertEquals(28, player4.getPoints());
        assertTrue(pointHandler.getWinner().get(0).contains(player4) && pointHandler.getWinner().get(0).size()==1);
        assertTrue(pointHandler.getWinner().get(2).contains(player2) && pointHandler.getWinner().get(2).size()==1);
        assertTrue(pointHandler.getWinner().get(3).contains(player3) && pointHandler.getWinner().get(3).size()==1);
        assertTrue(pointHandler.getWinner().get(1).contains(player1) && pointHandler.getWinner().get(1).size()==1);
        assertEquals(5, player1.getShots().size());
        assertEquals(6, pointHandler.getDeaths().size());

    }


}
