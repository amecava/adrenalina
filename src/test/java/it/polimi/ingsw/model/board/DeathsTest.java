package it.polimi.ingsw.model.board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.points.PointHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeathsTest {

    @Test
    void endGame() {

        List<Player> playerList = new ArrayList<>();
        Player player1 = new Player("Jacopo", Color.BLUE);
        playerList.add(player1);
        Player player2 = new Player("Federico", Color.YELLOW);
        playerList.add(player2);
        Player player3 = new Player("Amedeo", Color.GREEN);
        playerList.add(player3);
        Player player4 = new Player(" giulia ", Color.VIOLET);
        playerList.add(player4);
        PointHandler pointHandler = new PointHandler(playerList, 2);
        for (int i = 0; i < 8; i++) {
            player3.damagePlayer(player2.getPlayerColor());
        }
        for (int i = 0; i < 4; i++) {
            player3.damagePlayer(player1.getPlayerColor());
        }
        pointHandler.checkIfDead();
        for (Player player5: playerList){
            System.out.println( ""+player5.getPoints()+ player5.getPlayerColor());
        }
        for (int i = 0; i < 3; i++) {
            player3.damagePlayer(player2.getPlayerColor());
        }
        for (int i = 0; i < 2; i++) {
            player3.damagePlayer(player1.getPlayerColor());
        }
        for (int i = 0; i < 10; i++) {
            player3.damagePlayer(player2.getPlayerColor());
        }
        player1.damagePlayer(player3.getPlayerColor());
        player2.damagePlayer(player3.getPlayerColor());
        pointHandler.checkIfDead();
        player2.setPoints(-3);
        player3.setPoints(+10);
        player4.setPoints(+28);
        for (Player p: playerList){
            System.out.println(p.getPoints());
        }
        assertEquals(player1.getPoints(), 18);
        assertEquals(player2.getPoints(), 19);
        assertEquals(player3.getPoints(), 28);
    }
}