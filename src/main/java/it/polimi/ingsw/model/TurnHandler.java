package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.model.exceptions.jacop.FrenzyRegenerationException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Adrenalin;
import it.polimi.ingsw.model.points.PointHandler;
import java.util.ArrayList;
import java.util.List;

public class TurnHandler {

    private boolean frenzy;
    private boolean gameStarted;
    private Board board;
    private Player activePlayer;
    private Player firstFrenzyPlayer;
    private List<Player> playerList;
    private PointHandler pointHandler;
    private EffectHandler effectHandler;

    // player must type end action before reloading end end turn in order to finish his turn!!
    public TurnHandler(Board board, EffectHandler effectHandler, int numberOfDeaths) {
        this.board = board;
        this.playerList = new ArrayList<>();
        this.pointHandler = new PointHandler(this.playerList, numberOfDeaths);
        this.frenzy = false;
        this.gameStarted = false;
        this.effectHandler = effectHandler;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void addPlayer(Player player) {
        this.playerList.add(player);
    }

    public void enableFrenzy() {
        this.pointHandler.enableFrenzy();
    }


    public synchronized void setActivePlayer(Player activePlayer) {

        while (this.playerList.stream().anyMatch(
                x -> x.isRespawn() || (x.isActivePlayer() && x.getCurrentPosition() == null))) {
            try {
                this.wait();
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }
        if (this.getActivePlayer()!=null)
            this.activePlayer.setActivePlayer(false);
        this.activePlayer = activePlayer;
        this.activePlayer.setActivePlayer(true);

        this.effectHandler.setActivePlayer(activePlayer);

        if (this.activePlayer.getAdrenalin().equals(Adrenalin.SECONDFRENZY)) {

            this.activePlayer.setRemainingActions(1);
        } else {

            this.activePlayer.setRemainingActions(2);
        }
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

            this.firstFrenzyPlayer = this.playerList.get(this.getNextPlayer(
                    this.activePlayer));//frenzy player is useful because after 1 turn of every people from the first frenzy player the game ends
        } else {
            if (playerList.get(this.getNextPlayer(this.activePlayer)) == firstFrenzyPlayer) {
                this.pointHandler.endGame();
            }
        }
    }

    public int getNextPlayer(Player activePlayer) {

        int nextPlayer = playerList.indexOf(activePlayer) + 1;

        if (nextPlayer == playerList.size()) {

            nextPlayer = 0;
        }

        return nextPlayer;
    }

    // presenter needs to catch the end of game exception and block everything !!!
    //endOfTurn called by the presenter!!!!
    public void endOfTurn() throws EndGameException {
        this.activePlayer.endAction();
        this.activePlayer.setRemainingActions(-1);
        this.playerList.stream()
                .filter(Player::isDead)
                .forEach(x -> {
                    x.removePlayerFromBoard();
                    x.addPowerUp(this.board.getPowerUp());
                });
        this.board.fillBoard();
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();
        } catch (FrenzyRegenerationException e) {

            this.frenzyRound();
        }
    }

    public void startGame(int index) throws IllegalActionException {

        if (!gameStarted && index >= 0 && index < this.playerList.size()) {

            gameStarted = true;
            this.playerList.forEach(x ->
                    x.addPowerUp(this.board.getPowerUp()));
            this.playerList.forEach(x ->
                    x.addPowerUp(this.board.getPowerUp()));
            this.playerList.get(index).setFirstPlayer(true);
            this.setActivePlayer(this.playerList.get(index));
        } else {
            throw new IllegalActionException(" game already started!!");
        }
    }

    public List<Player> getPlayerList() {
        return this.playerList;
    }
}