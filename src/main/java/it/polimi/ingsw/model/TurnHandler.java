package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.PowerUpCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.model.exceptions.jacop.FrenzyRegenerationException;
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
    private EffectHandler effectHandler;
    private int turnNumber;
    private boolean canRespawn;

    // player must type end action before reloading end end turn in order to finish his turn!!
    public TurnHandler(Board board,
            PointHandler pointHandler, EffectHandler effectHandler) {
        this.board = board;
        this.pointHandler = pointHandler;
        this.playerList = pointHandler.getPlayerList();
        this.frenzy = false;
        this.gameStarted = false;
        this.effectHandler = effectHandler;
        this.turnNumber = 1;
        this.canRespawn = false;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    private void setActivePlayer(Player activePlayer) {

        this.activePlayer = activePlayer;
        this.effectHandler.setActivePlayer(activePlayer);
    }

    private void frenzyRound() throws EndGameException {
        this.turnNumber++;
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
            this.firstFrenzyPlayer = activePlayer;//frenzy player is useful because after 1 turn of every people from the first frenzy player the game ends
        } else {

            if (playerList.get(this.getNextPlayer(this.activePlayer)) == firstFrenzyPlayer) {
                this.pointHandler.endGame();
            }
            this.setActivePlayer(playerList.get(this.getNextPlayer(this.activePlayer)));
        }
        if (this.activePlayer.getAdrenalin().equals(Adrenalin.FIRSTFRENZY)) {

            this.activePlayer.setRemainingActions(2);
        } else {

            this.activePlayer.setRemainingActions(1);
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
    public void endOfTurn() throws EndGameException, IllegalActionException {
        this.activePlayer.endAction();
        this.activePlayer.setRemainingActions(0);
        this.playerList.stream().filter(x -> x.isDead() && !canRespawn)
                .forEach(x -> {
                    x.removePlayerFromBoard();
                    x.addPowerUp(this.board.getPowerUp());
                });
        this.canRespawn = true;
        if (!this.canFinishTurn()) {
            throw new IllegalActionException(" please wait for all players to spawn back!!");
        }
        this.canRespawn=false;
        this.board.fillBoard();
        try {
            pointHandler.checkIfDead();
            pointHandler.countKills();
        } catch (FrenzyRegenerationException e) {

            this.frenzyRound();

            return;
        }

        this.setActivePlayer(playerList.get(this.getNextPlayer(this.activePlayer)));
        this.activePlayer.setRemainingActions(2);
        this.turnNumber++;
    }


    public boolean canFinishTurn() {
        //first round for every player!!!!
        if (this.turnNumber <= this.playerList.size()) {
            for (Player player : playerList) {
                if (player.isFirstPlayer()) {
                    while (player != activePlayer) {
                        if (player.getCurrentPosition() == null) {
                            System.out.println(player.getPlayerId());
                            return false;
                        }
                        player = playerList.get(this.getNextPlayer(player));
                    }
                    return activePlayer.getCurrentPosition()!=null; // needs to be checked because otherwise i can end the turn without being in the map!!
                }
            }
        }
        for (Player player : playerList) {
            if (player.getCurrentPosition() == null) {
                return false;
            }
        }
        return  true;
    }

    public void startGame(Player firstPlayer) throws IllegalActionException {

        if (!gameStarted) {

            gameStarted = true;
            this.playerList.stream().forEach(x ->
                    x.addPowerUp(this.board.getPowerUp()));
            this.playerList.stream().forEach(x ->
                    x.addPowerUp(this.board.getPowerUp()));
            firstPlayer.setFirstPlayer(true);
            this.setActivePlayer(firstPlayer);
        }
        else throw new IllegalActionException(" game already started!!");
    }

    //presenter needs to check if the active player is really calling spawn!!!!
    public  void spawn(PowerUpCard powerUpCard, Player player) throws IllegalActionException {
        if ((this.turnNumber <=playerList.size() && player == this.activePlayer) || (this.canRespawn && player
                .isDead())) {
            powerUpCard.setOwner(null);
            player.getPowerUpsList().remove(powerUpCard);
            this.board.getPowerUpDeck().addPowerUpCard(powerUpCard);
            player.movePlayer(this.board.getRoomsList().stream()
                    .filter(x -> x.getColor().equals(powerUpCard.getColor()))
                    .flatMap(x -> x.getSquaresList().stream()).filter(x -> x.isSpawn())
                    .findFirst()
                    .get());
        } else {
            throw new IllegalActionException(" can't respawn now!!!");
        }
    }


}