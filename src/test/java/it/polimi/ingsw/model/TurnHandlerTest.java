package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.endGameException.EndGameException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.points.PointHandler;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TurnHandlerTest {

    EffectHandler effectHandler = new EffectHandler();


    @Test
    void CorrectEndOfTurn() {
        Player player1 = new Player("jacopo", Color.VIOLET);
        Player player2 = new Player("Amedeo", Color.GREEN);
        Player player3 = new Player("federico", Color.YELLOW);
        Player player4 = new Player("Giulia ", Color.LIGHTBLUE);
        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
        playerList.add(player4);
        PointHandler pointHandler = new PointHandler(playerList, 3);
        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);
        TurnHandler turnHandler = new TurnHandler(board, pointHandler, effectHandler);
        pointHandler.enableFrenzy();
        try {
            turnHandler.startGame(playerList.get(0));
        } catch (IllegalActionException e) {
            fail();
        }
        for(Player player : playerList){
            assertTrue(player.getPowerUpsList().size()==2);
        }
        try {
            turnHandler.startGame(playerList.get(0));
        }
        catch (IllegalActionException e) {
            assertTrue(true);
        }
        try {
            turnHandler.selectAction(3);
        } catch (IllegalActionException e) {
            assertTrue(true);
        }
        try {
            turnHandler.spawn(player1.getPowerUpsList().get(0), player1);
        } catch (IllegalActionException e) {
            fail();
        }
        for (Player player : playerList) {
            if (player != player1) {
                assertTrue(player == turnHandler.getActivePlayer());
                try {
                    turnHandler.endOfTurn();
                } catch (EndGameException e) {
                    // e.printStackTrace();
                } catch (IllegalActionException e) {
                    try {
                        turnHandler.spawn(player.getPowerUpsList().get(0), player);
                        assertTrue(player.getCurrentPosition()!=null);
                    } catch (IllegalActionException e1) {
                        e1.printStackTrace();
                        fail();
                    }
                    try {
                        turnHandler.endOfTurn();
                    } catch (EndGameException e1) {
                        fail();
                    } catch (IllegalActionException e1) {
                        fail();
                    }
                }
            }
            else{
                try {
                    turnHandler.endOfTurn();
                } catch (EndGameException e) {
                    fail();
                } catch (IllegalActionException e) {
                    fail();
                }
            }
        }

        try {
            turnHandler.selectAction(3);
        } catch (IllegalActionException e) {
           // e.printStackTrace();
        }
        for (int i = 0; i < 11; i++) {
            player1.damagePlayer(player4.getColor(), true);
            player2.damagePlayer(player4.getColor(), true);
            player3.damagePlayer(player4.getColor(), true);
        }
        try {
            turnHandler.endOfTurn();
        } catch (EndGameException e) {
            fail();
           // e.printStackTrace();
        } catch (IllegalActionException e) {
            assertTrue(true);
        }
        try {
            turnHandler.spawn(player1.getPowerUpsList().get(0), player1);
            turnHandler.spawn(player2.getPowerUpsList().get(0), player2);
            turnHandler.spawn(player3.getPowerUpsList().get(0), player3);
            assertTrue(player1.getCurrentPosition()!=null);
            assertTrue(player2.getCurrentPosition()!=null);
            assertTrue(player3.getCurrentPosition()!=null);
        } catch (IllegalActionException e) {
            fail();
        }
        try {
            turnHandler.endOfTurn();
        } catch (EndGameException e) {
            fail();
        } catch (IllegalActionException e) {
            e.printStackTrace();
            fail();
        }

        for (int i = 1; i < playerList.size()-1; i++) {
            assertTrue(turnHandler.getActivePlayer() == playerList.get(i));
            try {
                turnHandler.endOfTurn();
            }
            catch (EndGameException e){
                fail();
                //e.printStackTrace();
            } catch (IllegalActionException e) {
                fail();
            }


        }
        try{turnHandler.endOfTurn();}
        catch (EndGameException e){
           fail();

        } catch (IllegalActionException e) {
            e.printStackTrace();
        }
        try{turnHandler.endOfTurn();}
        catch (EndGameException e){
            System.out.println("game ended !!!");
            // System.out.println(e);
            assertTrue(true);

        } catch (IllegalActionException e) {
            fail();
        }


    }

}