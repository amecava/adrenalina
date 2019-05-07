package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.ammo.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.PowerUpCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.presenter.TurnChanger;
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
        Board board = new Board.BoardBuilder(new EffectHandler()).build(0);
        board.fillBoard();
        TurnHandler turnHandler = new TurnHandler(board, effectHandler, 3);
        turnHandler.addPlayer(player1);
        turnHandler.addPlayer(player2);
        turnHandler.addPlayer(player3);
        turnHandler.addPlayer(player4);
        turnHandler.enableFrenzy();
        try {
            turnHandler.startGame(0);
        } catch (IllegalActionException e) {
            fail();
        }
        try {
            PowerUpCard tmp = turnHandler.getActivePlayer()
                    .spawn(turnHandler.getActivePlayer().getPowerUpsList().get(0));
            board.getPowerUpDeck().addPowerUpCard(tmp);
            turnHandler.getActivePlayer().movePlayer(
                    board.getRoomsList().stream().filter(x -> x.getColor().equals(tmp.getColor()))
                            .flatMap(x -> x.getSquaresList().stream()).filter(x -> x.isSpawn())
                            .findFirst().get());
        } catch (IllegalActionException e) {
            fail();
        }
        try {
            turnHandler.endOfTurn();
            TurnChanger turnChanger = new TurnChanger(turnHandler);
            turnChanger.start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (EndGameException e) {
            fail();
        }
        assertTrue(player2.isActivePlayer());
        try {
            PowerUpCard tmp2 = player2.spawn(player2.getPowerUpsList().get(0));
            turnHandler.getActivePlayer().movePlayer(
                    board.getRoomsList().stream().filter(x -> x.getColor().equals(tmp2.getColor()))
                            .flatMap(x -> x.getSquaresList().stream()).filter(x -> x.isSpawn())
                            .findFirst().get());
        } catch (IllegalActionException e) {
            fail();
        }
        try {
            turnHandler.endOfTurn();
        } catch (EndGameException e) {
            fail();
        }
        TurnChanger turnChanger2 = new TurnChanger(turnHandler);
        turnChanger2.start();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(turnHandler.getActivePlayer() == player3);
        try {
            PowerUpCard powerUpCard3 = player3.spawn(player3.getPowerUpsList().get(0));
            turnHandler.getActivePlayer().movePlayer(
                    board.getRoomsList().stream()
                            .filter(x -> x.getColor().equals(powerUpCard3.getColor()))
                            .flatMap(x -> x.getSquaresList().stream()).filter(x -> x.isSpawn())
                            .findFirst().get());

        } catch (IllegalActionException e) {
            fail();
        }
        for (int i = 0; i < 11; i++) {
            player1.damagePlayer(player3.getColor(), false);
            player2.damagePlayer(player3.getColor(), false);
        }
        TurnChanger turnChanger3 = new TurnChanger(turnHandler);
        try {
            turnHandler.endOfTurn();
            turnChanger3.start();
        } catch (EndGameException e) {
            fail();
        }
        try {
            player1.addPowerUp(board.getPowerUpDeck().getPowerUpCard());
            player2.addPowerUp(board.getPowerUpDeck().getPowerUpCard());

            PowerUpCard powerUpCard4 = player1.spawn(player1.getPowerUpsList().get(0));
            player1.movePlayer(
                    board.getRoomsList().stream()
                            .filter(x -> x.getColor().equals(powerUpCard4.getColor()))
                            .flatMap(x -> x.getSquaresList().stream()).filter(x -> x.isSpawn())
                            .findFirst().get());
            PowerUpCard powerUpCard5 = player2.spawn(player2.getPowerUpsList().get(0));
            player2.movePlayer(
                    board.getRoomsList().stream()
                            .filter(x -> x.getColor().equals(powerUpCard5.getColor()))
                            .flatMap(x -> x.getSquaresList().stream()).filter(x -> x.isSpawn())
                            .findFirst().get());
            assertTrue(player1.getCurrentPosition()!=null);
            assertTrue(player2.getCurrentPosition()!=null);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (turnHandler) {
                turnHandler.notifyAll();
            }
        } catch (IllegalActionException e) {
            e.printStackTrace();
            fail();
        }
        try {
            turnChanger3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(player4.isActivePlayer());
        try {
            turnHandler.endOfTurn();
        } catch (EndGameException e) {
            fail();
        }
        TurnChanger turnChanger = new TurnChanger(turnHandler);
        turnChanger.start();
        assertTrue(turnHandler.getActivePlayer() == player4);
        try {
            PowerUpCard tmp4 = player4.spawn(player4.getPowerUpsList().get(0));
            turnHandler.getActivePlayer().movePlayer(
                    board.getRoomsList().stream().filter(x -> x.getColor().equals(tmp4.getColor()))
                            .flatMap(x -> x.getSquaresList().stream()).filter(x -> x.isSpawn())
                            .findFirst().get());
            Thread.sleep(250);
            synchronized (turnHandler) {
                turnHandler.notify();
            }


        } catch (IllegalActionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            turnChanger.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(turnHandler.getActivePlayer() == player1);

        for (int i = 0; i < 11; i++) {
            player2.damagePlayer(player1.getColor(), false);
            player3.damagePlayer(player1.getColor(), false);
            player4.damagePlayer(player1.getColor(), false);
        }
        try {
            for (Player player : turnHandler.getPlayerList()){
                if (player.getCurrentPosition()==null){
                }
            }
            turnHandler.endOfTurn();
        } catch (EndGameException e) {
            e.printStackTrace();
        }
        TurnChanger turnChanger9= new TurnChanger(turnHandler);
        turnChanger9.start();
        assertTrue(turnHandler.getActivePlayer() == player1);
        for (int i = 1; i < 4 && player1.isActivePlayer(); i++) {
            try {
                PowerUpCard powerUpCard7 = playerList.get(i)
                        .spawn(playerList.get(i).getPowerUpsList().get(0));
                playerList.get(i).movePlayer( board.getRoomsList().stream().filter(x -> x.getColor().equals(powerUpCard7.getColor()))
                        .flatMap(x -> x.getSquaresList().stream()).filter(x -> x.isSpawn())
                        .findFirst().get());
                synchronized (turnHandler){
                    turnHandler.notifyAll();
                }
                Thread.sleep(250);
            } catch (IllegalActionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertTrue(player2.isActivePlayer());

    }

}