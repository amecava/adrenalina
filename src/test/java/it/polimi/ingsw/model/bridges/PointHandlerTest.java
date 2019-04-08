package it.polimi.ingsw.model.bridges;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.Deaths;
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
        pointHandler.setDeaths(new Deaths(4));
        for (int i=0; i<5; i++ ) {
            player1.setMark(player2, 1);
            player1.setDamage(player3, 1);
        }
        System.out.println("player 1 marks  "+ player1.getBridge().getDamageBridge().getMarkers());
        player1.setDamage(player2, 4);
        pointHandler.checkIfdead();
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
        pointHandler.setDeaths(new Deaths(4));
        for (int i=0; i<5; i++ ) {
            player1.setMark(player2, 1);
            player1.setMark(player3, 1);
        }
        System.out.println("player 1 marks  "+ player1.getBridge().getDamageBridge().getMarkers());
        player1.setDamage(player2, 4);
        player1.setDamage(player4, 1);
        player1.setDamage(player3,2);
        player1.setMark(Color.LIGHTBLUE,2);
        pointHandler.checkIfdead();
        for (PointStructure pointStructure: pointHandler.getPointStructures()) {
            System.out.println(pointStructure);
        }
        player1.setDamage(player2,6);
        player1.setDamage(player3,6);
        pointHandler.checkIfdead();
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
        System.out.println("deaths of the game  " + pointHandler.getDeaths() );
        assertTrue(player2.getPoints()==16);
        assertTrue(player3.getPoints()==10);
        assertTrue(player3.getBridge().getDamageBridge().getMarkers().size()==2);
        assertTrue(player4.getPoints()==4);
    }
    }
