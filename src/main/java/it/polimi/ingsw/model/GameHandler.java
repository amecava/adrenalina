package it.polimi.ingsw.model;

import it.polimi.ingsw.model.players.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Adrenalin;
import it.polimi.ingsw.model.points.PointHandler;
import it.polimi.ingsw.presenter.exceptions.LoginException;
import java.util.ArrayList;
import java.util.List;

public class GameHandler {

    private String gameId;
    private boolean gameStarted = false;

    private List<Player> playerList = new ArrayList<>();

    private Board board;

    private PointHandler pointHandler;
    private EffectHandler effectHandler = new EffectHandler();

    private boolean frenzy = false;

    private Player activePlayer;
    private Player firstFrenzyPlayer;

    public GameHandler(String gameId) {

        this.gameId = gameId;
    }

    public String getGameId() {

        return this.gameId;
    }

    public void createBoard(int id) {

        this.board = new Board.BoardBuilder(this.effectHandler).build(id);
    }

    public void selectNumberOfDeaths(int numberOfDeaths) {

        this.pointHandler = new PointHandler(this.playerList, numberOfDeaths);
    }

    public List<Player> getPlayerList() {

        return this.playerList;
    }

    public Player addPlayer(String playerId, String character) throws LoginException {

        if (Color.ofCharacter(character) == null) {

            throw new LoginException("Il personaggio selezionato non esiste.");
        }

        if (this.playerList.stream().anyMatch(x -> x.getColor().equals(Color.ofCharacter(character)))) {

            throw new LoginException("Il personaggio selezionato è già stato selezionato.");
        }

        Player player = new Player(playerId, Color.ofCharacter(character));

        this.playerList.add(player);

        return player;
    }

    public Player getActivePlayer() {

        return this.activePlayer;
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
        if (this.activePlayer != null)
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

    public int getNextPlayer(Player player) {

        int nextPlayer = playerList.indexOf(player) + 1;

        if (nextPlayer == playerList.size()) {

            nextPlayer = 0;
        }

        return nextPlayer;
    }

    public void endOfTurn() throws EndGameException {

        this.activePlayer.endAction();
        this.activePlayer.setRemainingActions(-1);
        this.playerList.stream()
                .filter(Player::isDead)
                .forEach(x -> {
                    x.removePlayerFromBoard();
                    x.addPowerUp(this.board.getPowerUp());
                });

        this.pointHandler.checkIfDead();
        this.pointHandler.countKills();

        if (this.pointHandler.checkEndGame()) {

            throw new EndGameException(this.pointHandler.endGame());
        }

        this.board.fillBoard();
    }

    public void startGame(int index) throws IllegalActionException {

        if (!this.gameStarted && index >= 0 && index < this.playerList.size()) {

            this.gameStarted = true;
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
}
