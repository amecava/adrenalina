package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.PowerUpCard;
import it.polimi.ingsw.model.players.Color;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Adrenalin;
import it.polimi.ingsw.model.points.PointHandler;
import it.polimi.ingsw.presenter.ClientHandler;
import it.polimi.ingsw.presenter.Presenter;
import it.polimi.ingsw.presenter.exceptions.LoginException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class GameHandler {

    private String gameId;
    private boolean gameStarted = false;

    private List<Player> playerList = new ArrayList<>();

    private Board board;

    private PointHandler pointHandler;
    private EffectHandler effectHandler = new EffectHandler();

    private Player activePlayer;

    public GameHandler(String gameId, int numberOfDeaths, boolean frenzy) {

        this.gameId = gameId;

        this.pointHandler = new PointHandler(this.playerList, numberOfDeaths);
        this.pointHandler.setFrienzy(frenzy);
    }

    public String getGameId() {

        return this.gameId;
    }

    public List<Player> getPlayerList() {

        return this.playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public void createBoard(int id) {

        this.board = new Board.BoardBuilder(this.effectHandler).build(id);
    }

    public Player addPlayer(String playerId, String character) throws LoginException {

        if (Color.ofCharacter(character) == null) {

            throw new LoginException("Il personaggio selezionato non esiste.");
        }

        if (this.playerList.stream()
                .anyMatch(x -> x.getColor().equals(Color.ofCharacter(character)))) {

            throw new LoginException("Il personaggio selezionato è già stato preso.");
        }

        Player player = new Player(playerId, Color.ofCharacter(character));

        this.playerList.add(player);

        return player;
    }

    public Player getActivePlayer() {

        return this.activePlayer;
    }

    public synchronized void canFinishTurn(Player activePlayer) {
        long elapsedTime = 0;
        long remainingTimeToWait = 20000;
        LocalDateTime startingTime;
        long oldNumberOfPlayerInRespawn;
        ClientHandler.gameBroadcast(this,
                x -> x.getKey().isRespawn() || (x.getKey().isActivePlayer()
                        && x.getKey().getCurrentPosition() == null), "infoMessage",
                "Devi fare il respawn, utilizza il comando spawn nomePowerUp colorePowerUp.");
        while (this.numberOfPlayerInRespawn() > 0) {
            try {
                oldNumberOfPlayerInRespawn = this.numberOfPlayerInRespawn();
                startingTime = LocalDateTime.now();

                if (remainingTimeToWait > 0) {
                    this.wait(remainingTimeToWait);
                }
                if (oldNumberOfPlayerInRespawn > this
                        .numberOfPlayerInRespawn()) {

                    elapsedTime = Duration.between(startingTime, LocalDateTime.now()).toMillis();
                    remainingTimeToWait = remainingTimeToWait - elapsedTime;

                } else {

                    this.playerList.stream().filter(x -> x.isRespawn() || (x.isActivePlayer()
                            && x.getCurrentPosition() == null)).forEach(x -> {

                        Presenter presenter = ClientHandler.getPresenter(this, y -> y.equals(x));

                        //presenter.spawn("random");
                        //presenter needs to see if it's called with the keyWord random

                        ClientHandler.gameBroadcast(this, y -> y.getKey().equals(x), "infoMessage",
                                "Sei stato respawnato in modo casuale per via del timer scaduto.");


                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.setActivePlayer(activePlayer);
    }

    private void setActivePlayer(Player activePlayer) {
        if (this.activePlayer != null) {
            this.activePlayer.setActivePlayer(false);
        }
        this.activePlayer = activePlayer;
        this.activePlayer.setActivePlayer(true);

        this.effectHandler.setActivePlayer(activePlayer);

        if (this.activePlayer.getAdrenalin().equals(Adrenalin.SECONDFRENZY)) {

            this.activePlayer.setRemainingActions(1);
        } else {

            this.activePlayer.setRemainingActions(2);
        }
    }

    public int numberOfPlayerInRespawn() {
        return (int) this.playerList.stream()
                .filter(x -> x.isRespawn() || (x.isActivePlayer()
                        && x.getCurrentPosition() == null)).count();

    }

    public void endOfTurn() throws EndGameException, IllegalActionException {
        if (this.activePlayer.getRemainingActions() == -1) {
            throw new IllegalActionException(" you have already finished your turn!!");
        }
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

    public JsonObject toJsonObject() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        this.playerList.stream()
                .map(Player::toJsonObject)
                .forEach(builder::add);

        return Json.createObjectBuilder()
                .add("gameId", this.gameId)
                .add("numberOfDeaths", this.pointHandler.getNumberOfDeaths())
                .add("frenzy", this.pointHandler.isFrenzy())
                .add("playerList", builder.build())
                .build();
    }
}
