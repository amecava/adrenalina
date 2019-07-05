package it.polimi.ingsw.server.model.players.bridges;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.model.points.Deaths;
import it.polimi.ingsw.server.model.points.PointHandler;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the player's ActionBridge.
 */
class ActionBridgeTest {

    /**
     * Tests if the Adrenaline states are correctly changed during the game.
     */
    @Test
    void ActionTest() {
        Player player1 = new Player("jacopo", Color.BLU);
        Player player2 = new Player("Amedeo", Color.VERDE);
        Player player3 = new Player("federico", Color.GIALLO);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        Deaths deaths = new Deaths(4);
        for (int i = 0; i < 2; i++) {
            player1.damagePlayer(player2.getColor(), true);
            player3.damagePlayer(player2.getColor(), true);
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
        assertTrue(player1.getAdrenalin() == Adrenalin.NORMAL);
        for (int i = 0; i < 3; i++) {
            player1.damagePlayer(player2.getColor(), true);
            player3.damagePlayer(player2.getColor(), true);
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

        assertTrue(player1.getAdrenalin() == Adrenalin.FIRSTADRENALIN);
        assertTrue(player3.getAdrenalin() == Adrenalin.FIRSTADRENALIN);

        for (int i = 0; i < 5; i++) {
            player1.damagePlayer(player2.getColor(), true);
            player3.damagePlayer(player2.getColor(), true);
        }
        player3.damagePlayer(player1.getColor(), true);
        try {
            PointHandler.checkIfDead(playerList);
            PointHandler.countKills(deaths, playerList);

            if (PointHandler.checkEndGame(deaths, playerList)) {

                throw new EndGameException(PointHandler.endGame(deaths, playerList));
            }

        } catch (EndGameException e) {
            fail();
        }
        assertTrue(player1.getAdrenalin() == Adrenalin.SECONDADRENALIN);
        assertTrue(player3.getAdrenalin() == Adrenalin.NORMAL);
    }
}