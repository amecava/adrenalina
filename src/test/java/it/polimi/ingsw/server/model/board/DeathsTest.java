package it.polimi.ingsw.server.model.board;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.points.Deaths;
import it.polimi.ingsw.server.model.points.PointHandler;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the killStreak.
 */
class DeathsTest {

    /**
     * Tests the game ending, that launches an EndGameException.
     */
    @Test
    void endGame() {

        List<Player> playerList = new ArrayList<>();
        Player player1 = new Player("Jacopo", Color.BLU);
        playerList.add(player1);
        Player player2 = new Player("Federico", Color.GIALLO);
        playerList.add(player2);
        Player player3 = new Player("Amedeo", Color.VERDE);
        playerList.add(player3);
        Player player4 = new Player(" giulia ", Color.VIOLA);
        playerList.add(player4);

        Deaths deaths = new Deaths(2);

        for (int i = 0; i < 8; i++) {
            player3.damagePlayer(player2.getColor(), true);
        }
        for (int i = 0; i < 4; i++) {
            player3.damagePlayer(player1.getColor(), true);
        }
        try {
            PointHandler.checkIfDead(playerList);
            PointHandler.countKills(deaths, playerList);
            if (PointHandler.checkEndGame(deaths, playerList)) {

                throw new EndGameException(PointHandler.endGame(deaths, playerList));
            }
        } catch (EndGameException e) {
            fail();
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
            PointHandler.checkIfDead(playerList);
            PointHandler.countKills(deaths, playerList);

            if (PointHandler.checkEndGame(deaths, playerList)) {

                throw new EndGameException(PointHandler.endGame(deaths, playerList));
            }
            fail();
        } catch (EndGameException e) {

            player2.setPoints(-3);
            player3.setPoints(+10);
            player4.setPoints(+28);
            assertEquals(player1.getPoints(), 18);
            assertEquals(player2.getPoints(), 19);
            assertEquals(player3.getPoints(), 28);

        }
    }
}