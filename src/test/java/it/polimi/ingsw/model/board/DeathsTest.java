package it.polimi.ingsw.model.board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.endGameException.EndGameException;
import it.polimi.ingsw.model.exceptions.endGameException.FrenzyRegenerationException;
import it.polimi.ingsw.model.points.PointHandler;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeathsTest {

    @Test
    void endGame() {
        EffectHandler effectHandler = new EffectHandler();
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
            player3.damagePlayer(player2.getColor(), true);
        }
        for (int i = 0; i < 4; i++) {
            player3.damagePlayer(player1.getColor(), true);
        }
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();
        } catch (FrenzyRegenerationException e) {
           // e.printStackTrace();
        } catch (EndGameException e) {
           // e.printStackTrace();
        }
        for (int i = 0; i < 3; i++) {
            player3.damagePlayer(player2.getColor(), true);
        }
        for (int i = 0; i < 2; i++) {
            player3.damagePlayer(player1.getColor(), true);
        }
        for (int i = 0; i < 10; i++) {
            player3.damagePlayer(player2.getColor(), true);
        }
        player1.damagePlayer(player3.getColor(), true);
        player2.damagePlayer(player3.getColor(), true);
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();
        } catch (FrenzyRegenerationException e) {
           // e.printStackTrace();
        } catch (EndGameException e) {
           // e.printStackTrace();
            System.out.println("end Game!!!");
        }
        player2.setPoints(-3);
        player3.setPoints(+10);
        player4.setPoints(+28);
        assertEquals(player1.getPoints(), 18);
        assertEquals(player2.getPoints(), 19);
        assertEquals(player3.getPoints(), 28);
    }
}