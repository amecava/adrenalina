package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.model.players.bridges.Adrenalin;
import it.polimi.ingsw.server.model.points.PointHandler;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import java.util.ArrayList;
import java.util.List;
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

        if (this.playerList.stream().anyMatch(x -> x.getColor().equals(Color.ofCharacter(character)))) {

            throw new LoginException("Il personaggio selezionato è già stato preso.");
        }

        Player player = new Player(playerId, Color.ofCharacter(character));

        this.playerList.add(player);

        return player;
    }

    public Player getActivePlayer() {

        return this.activePlayer;
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
