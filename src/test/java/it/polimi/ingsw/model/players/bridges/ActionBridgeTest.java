package it.polimi.ingsw.model.players.bridges;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.points.PointHandler;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ActionBridgeTest {
    EffectHandler effectHandler=new EffectHandler();
    @Test
    void ActionTest(){
        Player player1 = new Player("jacopo", Color.BLUE, this.effectHandler);
        Player player2 = new Player("Amedeo", Color.GREEN, this.effectHandler);
        Player player3 = new Player("federico", Color.YELLOW, this.effectHandler);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        PointHandler pointHandler = new PointHandler(playerList, 4);
        for (Player player : playerList){
            player.getBridge().setAdrenalin(Adrenalin.SECONDADRENALIN);
            System.out.println(player.getPlayerId());
            for (ActionStructure actionStructure:player.getBridge().getActionBridge().getActionStructureList() )
                System.out.println(actionStructure.getId() + "" + actionStructure.getMove());

        }
        assertTrue(true);
    }


}