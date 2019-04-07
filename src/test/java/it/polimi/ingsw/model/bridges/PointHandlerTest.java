package it.polimi.ingsw.model.bridges;

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
        PointHandler pointHandler = new PointHandler(playerList);
        DamageBridge damageBridge1 = new DamageBridge(pointHandler, player1.getPlayerColor());
        Bridge bridge = new Bridge();
        bridge.setDamageBridge(damageBridge1);
        player1.setBridge(bridge);
        DamageBridge damageBridge2 = new DamageBridge(pointHandler, player2.getPlayerColor());
        Bridge bridge1 = new Bridge();
        bridge1.setDamageBridge(damageBridge2);
        player2.setBridge(bridge1);
        DamageBridge damageBridge3 = new DamageBridge(pointHandler, player3.getPlayerColor());
        Bridge bridge2= new Bridge();
        bridge2.setDamageBridge(damageBridge3);
        player3.setBridge(bridge2);
        for (int i=0; i<5; i++ ) {
            player1.setMark(player2, 1);
            player1.setDamage(player3, 1);
        }
        System.out.println("player 1 marks  "+ player1.getBridge().getDamageBridge().getMarkers());
        player1.setDamage(player2, 4);
        damageBridge1.checkIfDead();
        for (PointStructure pointStructure: pointHandler.getPointStructures()) {
            System.out.println(pointStructure);
        }
        System.out.println("marks of player 2 " +player1.getBridge().getDamageBridge().getMarkers());
        assertTrue(player2.getBridge().getDamageBridge().getMarkers().size()==1 &&
                   player2.getBridge().getDamageBridge().getMarkers().get(0).getColor().equals(player1.getPlayerColor()) );
        assertTrue(player2.getPoints()==8);
        assertTrue(player3.getPoints()==7);
    }




    @Test
    void multipleDeaths (){
        Player player1 = new Player("jacopo", Color.BLUE);
        Player player2 = new Player("Amedeo", Color.GREEN);
        Player player3 = new Player("federico", Color.YELLOW);
        Player player4= new Player("Giulia ", Color.LIGHTBLUE);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        PointHandler pointHandler = new PointHandler(playerList);
        DamageBridge damageBridge1 = new DamageBridge(pointHandler, player1.getPlayerColor());
        Bridge bridge = new Bridge();
        bridge.setDamageBridge(damageBridge1);
        player1.setBridge(bridge);
        DamageBridge damageBridge2 = new DamageBridge(pointHandler, player2.getPlayerColor());
        Bridge bridge1 = new Bridge();
        bridge1.setDamageBridge(damageBridge2);
        player2.setBridge(bridge1);
        DamageBridge damageBridge3 = new DamageBridge(pointHandler, player3.getPlayerColor());
        Bridge bridge2= new Bridge();
        bridge2.setDamageBridge(damageBridge3);
        player3.setBridge(bridge2);
        DamageBridge bridge3= new DamageBridge(pointHandler, player4.getPlayerColor());
        Bridge bridge4= new Bridge();
        bridge4.setDamageBridge(bridge3);
        player4.setBridge(bridge4);
        for (int i=0; i<5; i++ ) {
            player1.setMark(player2, 1);
            player1.setMark(player3, 1);
        }
        System.out.println("player 1 marks  "+ player1.getBridge().getDamageBridge().getMarkers());
        player1.setDamage(player2, 4);
        player1.setDamage(player4, 1);
        player1.setDamage(player3,2);
        player1.setMark(Color.LIGHTBLUE,2);
        damageBridge1.checkIfDead();
        for (PointStructure pointStructure: pointHandler.getPointStructures()) {
            System.out.println(pointStructure);
        }
        player1.setDamage(player2,6);
        player1.setDamage(player3,6);
        damageBridge1.checkIfDead();
        for (Player player: pointHandler.getPlayerList()){
            System.out.println("giocatore:"+
                    player.getPlayerColor()+
                    "shots"+
                    player.getBridge().getDamageBridge().getShots()+
                    "marks" +
                    player.getBridge().getDamageBridge().getMarkers()+
                    " number of deaths " + player.getBridge().getDamageBridge().getNumberOfDeaths()+
                    " player points " + player.getPoints());

        }

        assertTrue(player2.getPoints()==16);
        assertTrue(player3.getPoints()==10);
        assertTrue(player3.getBridge().getDamageBridge().getMarkers().size()==2);
        assertTrue(player4.getPoints()==4);
    }
    }
