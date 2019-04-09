package it.polimi.ingsw.model.board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.bridges.PointHandler;
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
        Player player4=  new Player(" giulia ", Color.VIOLET);
        playerList.add(player4);
        PointHandler pointHandler = new PointHandler(playerList, 2);
        pointHandler.setFrenzy(false);
        for (int i = 0; i < 8; i++) {
            player3.setDamage(player2, 1);
        }
        player3.setDamage(player1, 4);
        pointHandler.checkIfdead();
        pointHandler.setFrenzy(false);
        player3.setDamage(player2, 3);
        player3.setDamage(player1, 2);
        player3.setDamage(player2, 10);
        player1.setDamage(player3, 1);
        player2.setDamage(player3, 1);
        pointHandler.checkIfdead();
        player2.setPoints(-3);
        player3.setPoints(+10);
        player4.setPoints(+28);
        System.out.println( pointHandler.getDeaths());
        System.out.println(pointHandler.getWinner());
        System.out.println(player4.getPoints());
        assertTrue(player1.getPoints() == 18);
        assertTrue(player2.getPoints() == 19);
        assertTrue(player3.getPoints() == 28);
    }


}