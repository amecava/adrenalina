package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.endGameException.EndGameException;
import it.polimi.ingsw.model.exceptions.endGameException.FrenzyRegenerationException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Adrenalin;
import it.polimi.ingsw.model.points.PointHandler;
import java.util.List;

public class TurnHandler {

    private boolean frenzy;
    private boolean gameStarted;
    private Board board;
    private Player activePlayer;
    private Player firstFrenzyPlayer;
    private List<Player> playerList;
    private PointHandler pointHandler;
    private int remainingActions;
    private EffectHandler effectHandler;

    // player must type end action before reloading end end turn in order to finish his turn!!
    public TurnHandler(Board board,
            PointHandler pointHandler, EffectHandler effectHandler) {
        this.board = board;
        this.pointHandler = pointHandler;
        this.playerList = pointHandler.getPlayerList();
        this.frenzy = false;
        this.gameStarted = false;
        this.remainingActions = 2;
        this.effectHandler = effectHandler;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    private void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
        this.effectHandler.setActivePlayer(activePlayer);
    }

    private void frenzyRound() throws EndGameException {

        if (!this.frenzy) {

            this.frenzy = true;
            Player tempPlayer = this.playerList.get(this.getNextPlayer(this.activePlayer));

            while (!tempPlayer.isFirstPlayer()) {

                tempPlayer.setAdrenalin(Adrenalin.FIRSTFRENZY);
                tempPlayer = this.playerList.get(this.getNextPlayer(tempPlayer));
            }

            while (tempPlayer != this.activePlayer) {

                tempPlayer.setAdrenalin(Adrenalin.SECONDFRENZY);
                tempPlayer = playerList.get(this.getNextPlayer(tempPlayer));
            }

            this.setActivePlayer(playerList.get(this.getNextPlayer(this.activePlayer)));
            this.firstFrenzyPlayer = activePlayer;
        } else {

            if (playerList.get(this.getNextPlayer(this.activePlayer)) == firstFrenzyPlayer) {

                throw new EndGameException("end game!!!!", pointHandler.getWinner());
            }
            this.setActivePlayer(playerList.get(this.getNextPlayer(this.activePlayer)));
        }
        if (this.activePlayer.getAdrenalin().equals(Adrenalin.FIRSTFRENZY)) {

            this.remainingActions = 2;
        } else {

            this.remainingActions = 1;
        }
    }

    private int getNextPlayer(Player activePlayer) {

        int nextPlayer = playerList.indexOf(activePlayer) + 1;

        if (nextPlayer == playerList.size()) {

            nextPlayer = 0;
        }

        return nextPlayer;
    }

    // presenter needs to catch the end of game exception and block everything !!!
    //endOfTurn called by the presenter!!!!
    public void endOfTurn()
            throws EndGameException { //should be protected but for testing we use public!!
        this.endAction();
        this.board.endOfTurnFill();
        try {
            pointHandler.checkIfDead();
        } catch (FrenzyRegenerationException e) {
            this.frenzyRound();
            return;
        }
        this.setActivePlayer(playerList.get(this.getNextPlayer(this.activePlayer)));
        this.remainingActions = 2;
    }

    public void startGame(Player firstPlayer) throws IllegalAccessException {
        if (!gameStarted) {
            gameStarted = true;
            firstPlayer.setFirstPlayer(true);
            this.setActivePlayer(firstPlayer);
        } else {
            throw new IllegalAccessException("game already started!");
        }
    }

    //for all possible actions the presenter must see if the player that is  calling turnHandler is the activePlayer
    public void selectAction(int actionId) throws IllegalActionException {
        if (remainingActions > 0 || actionId==4) {//actionId=4 means reload!!
            this.activePlayer.selectAction(actionId - 1);
            if (actionId==4)
                this.remainingActions=0;
            else
                this.remainingActions--;

        } else {
            throw new IllegalActionException(
                    "no remaining actions, please type end of turn."
                            + "If you want to reload before finishing the turn please type end action "
                            + "followed  by  reload with the guns you would like to reload! ");
        }
    }

    private void endAction() {
        this.activePlayer.endAction();
    }


}