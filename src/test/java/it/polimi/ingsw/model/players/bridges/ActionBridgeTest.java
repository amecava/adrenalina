package it.polimi.ingsw.model.players.bridges;
/*
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.TurnHandler;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.model.exceptions.cards.SquareTypeException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.model.exceptions.jacop.FrenzyRegenerationException;
import it.polimi.ingsw.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
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
            player1.damagePlayer(player2.getColor(), true);
            player3.damagePlayer(player2.getColor(), true);
        }
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();
        } catch (FrenzyRegenerationException e) {
            e.printStackTrace();
        } catch (EndGameException e) {
            e.printStackTrace();
        }
        assertTrue(player1.getAdrenalin()==Adrenalin.NORMAL);
        assertTrue(player1.getActions().size()==4);
        assertTrue(player1.getActions().get(3).getId()==0);
        for (int i=0; i<3; i++){
            player1.damagePlayer(player2.getColor(), true);
            player3.damagePlayer(player2.getColor(), true);
        }
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();
        } catch (FrenzyRegenerationException e) {
            e.printStackTrace();
        } catch (EndGameException e) {
            e.printStackTrace();
        }
        assertTrue(player1.getAdrenalin()==Adrenalin.FIRSTADRENALIN);
        assertTrue(player3.getAdrenalin()==Adrenalin.FIRSTADRENALIN);
        for (int i=0; i<5; i++){
            player1.damagePlayer(player2.getColor(), true);
            player3.damagePlayer(player2.getColor(), true);
        }
        player3.damagePlayer(player1.getColor(), true);
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();
        } catch (FrenzyRegenerationException e) {
            e.printStackTrace();
        } catch (EndGameException e) {
            e.printStackTrace();
        }
        assertTrue(player1.getAdrenalin()==Adrenalin.SECONDADRENALIN);
        assertTrue(player3.getAdrenalin()==Adrenalin.NORMAL);

    }


    @Test
    void checkActions(){
        EffectHandler effectHandler = new EffectHandler();
        List<Player> playerList= new ArrayList<>();
        Player player1= new Player(" jacopo", Color.RED);
        Player player2 = new Player(" federico", Color.GRAY);
        Player player3= new Player(" Amedeo ", Color.YELLOW);
        Board board = new Board.BoardBuilder(effectHandler).build(0);
        board.fillBoard();
        TurnHandler turnHandler = new TurnHandler(board,effectHandler,5);
        turnHandler.addPlayer(player1);
        turnHandler.addPlayer(player2);
        turnHandler.addPlayer(player3);
        try {
            turnHandler.startGame(2);
        } catch (IllegalActionException e) {
            fail();
        }
        try {
            player1.selectAction(1);
        } catch (IllegalActionException e) {
            assertTrue(true);
        }
        try {
            turnHandler.getActivePlayer().spawn(player3.getPowerUpsList().get(0));
        } catch (IllegalActionException e) {
            fail();
        }
        try {
            turnHandler.getActivePlayer().selectAction(1);
        } catch (IllegalActionException e) {
            //e.printStackTrace();
            fail();
        }
        try {
            player3.move(new EffectArgument(player3.getCurrentPosition()), effectHandler);
        }
        catch (Exception e ){
            e.printStackTrace();
            fail();
        }
        try {
            player3.move(new EffectArgument(player3.getCurrentPosition()), effectHandler);
        }
        catch (Exception e ){
            assertTrue(true);
        }
        turnHandler.getActivePlayer().endAction();
        try {
            turnHandler.getActivePlayer().selectAction(2);
        } catch (IllegalActionException e) {
            fail();
        }
        try {
            player3.collect();
        } catch (SquareTypeException e) {
                assertTrue(true);
        } catch (EmptySquareException e) {
            fail();
        } catch (IllegalActionException e) {
            fail();
        }
        try {
            player3.move(new EffectArgument(player3.getCurrentPosition()), effectHandler);
        } catch (IllegalActionException e) {
            fail();
        } catch (EffectException e) {
            fail();
        } catch (PropertiesException e) {
            fail();
        }
        try {
            turnHandler.endOfTurn();
        } catch ( Exception e ){
            fail();
        } catch (EndGameException e) {
            fail();
        }
        try {
            turnHandler.getActivePlayer().spawn(player2.getPowerUpsList().get(0));
        } catch (IllegalActionException e) {
            assertTrue(true);
        }
        try {
            turnHandler.getActivePlayer().spawn(player1.getPowerUpsList().get(0));
        } catch (IllegalActionException e) {
            fail();

        }

        try {
            turnHandler.getActivePlayer().selectAction(2);
            player1.collect(((WeaponCard) player1.getCurrentPosition().getTools().get(0)).getId());
        } catch (IllegalActionException e) {
            fail();
        } catch (CardException e) {
            fail();
        }
        turnHandler.getActivePlayer().endAction();
        try {
            turnHandler. getActivePlayer().selectAction(2);
        } catch (IllegalActionException e) {
            fail();
        }
        turnHandler.getActivePlayer().endAction();
        try {
            turnHandler.getActivePlayer().selectAction(4);
        } catch (IllegalActionException e) {
            fail();
        }
        assertTrue(player1.getWeaponCardList().size()==1);



    }



}

 */