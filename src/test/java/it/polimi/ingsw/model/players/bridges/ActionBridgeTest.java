package it.polimi.ingsw.model.players.bridges;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.endGameException.EndGameException;
import it.polimi.ingsw.model.exceptions.endGameException.FrenzyRegenerationException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.points.PointHandler;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ActionBridgeTest {
    EffectHandler effectHandler=new EffectHandler();
    @Test
    void ActionTest(){
        Player player1 = new Player("jacopo", Color.BLUE);
        Player player2 = new Player("Amedeo", Color.GREEN);
        Player player3 = new Player("federico", Color.YELLOW);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        PointHandler pointHandler = new PointHandler(playerList, 4);
        for ( int i=0 ; i<2; i++){
            player1.damagePlayer(player2.getColor());
            player3.damagePlayer(player2.getColor());
        }
        try {
            pointHandler.checkIfDead();
        } catch (FrenzyRegenerationException e) {
            e.printStackTrace();
        } catch (EndGameException e) {
            e.printStackTrace();
        }
        assertTrue(player1.getAdrenalin()==Adrenalin.NORMAL);
        for (int i=0; i<3; i++){
            player1.damagePlayer(player2.getColor());
            player3.damagePlayer(player2.getColor());
        }
        try {
            pointHandler.checkIfDead();
        } catch (FrenzyRegenerationException e) {
            e.printStackTrace();
        } catch (EndGameException e) {
            e.printStackTrace();
        }
        assertTrue(player1.getAdrenalin()==Adrenalin.FIRSTADRENALIN);
        assertTrue(player3.getAdrenalin()==Adrenalin.FIRSTADRENALIN);
        for (int i=0; i<5; i++){
            player1.damagePlayer(player2.getColor());
            player3.damagePlayer(player2.getColor());
        }
        player3.damagePlayer(player1.getColor());
        try {
            pointHandler.checkIfDead();
        } catch (FrenzyRegenerationException e) {
            e.printStackTrace();
        } catch (EndGameException e) {
            e.printStackTrace();
        }
        assertTrue(player1.getAdrenalin()==Adrenalin.SECONDADRENALIN);
        assertTrue(player3.getAdrenalin()==Adrenalin.NORMAL);

    }


}