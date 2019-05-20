package it.polimi.ingsw.model;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.model.players.bridges.Adrenalin;
import it.polimi.ingsw.server.model.points.PointHandler;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Model {

    private List<Player> playerList = new ArrayList<>();

    private Board board;

    private PointHandler pointHandler;
    private EffectHandler effectHandler = new EffectHandler();

    private Player activePlayer;

    public Model(int numberOfDeaths, boolean frenzy) {

        this.pointHandler = new PointHandler(this.playerList, numberOfDeaths);
        this.pointHandler.setFrenzy(frenzy);
    }


    public List<Player> getPlayerList() {

        return this.playerList;
    }

    public void setPlayerList(List<Player> playerList) {

        this.playerList = playerList;
    }

    public void createBoard(int vote) {

        this.board = new Board.BoardBuilder(this.effectHandler).build(vote);
    }

    public Board getBoard() {

        return this.board;
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

    public synchronized void setActivePlayer(Player activePlayer) {

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

    public void startGame() {

        //TODO Random int

        this.playerList.forEach(x ->
                x.addPowerUp(this.board.getPowerUp()));
        this.playerList.forEach(x ->
                x.addPowerUp(this.board.getPowerUp()));
        this.playerList.get(0).setFirstPlayer(true);
        this.setActivePlayer(this.playerList.get(0));
    }

    public JsonObjectBuilder toJsonObject() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        this.playerList.stream()
                .map(Player::toJsonObject)
                .forEach(builder::add);

        return Json.createObjectBuilder()
                .add("numberOfDeaths", this.pointHandler.getNumberOfDeaths())
                .add("frenzy", this.pointHandler.isFrenzy())
                .add("playerList", builder.build());
    }
}
