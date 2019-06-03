package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.board.Board;
import it.polimi.ingsw.server.model.points.Deaths;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.exceptions.jacop.ColorException;
import it.polimi.ingsw.server.model.exceptions.jacop.EndGameException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.model.players.bridges.Adrenalin;
import it.polimi.ingsw.server.model.points.PointHandler;
import it.polimi.ingsw.server.presenter.exceptions.LoginException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class Model implements Serializable {

    private List<Player> playerList = new ArrayList<>();

    private Board board;
    private Deaths deaths;

    private EffectHandler effectHandler = new EffectHandler();

    private Player activePlayer;

    public Model(int numberOfDeaths, boolean frenzy) {

        this.deaths = new Deaths(numberOfDeaths);
        this.deaths.setFrenzy(frenzy);
    }


    public List<Player> getPlayerList() {

        return this.playerList;
    }

    public void createBoard(int vote) {

        this.board = new Board.BoardBuilder(this.effectHandler).build(vote);
    }

    public Board getBoard() {

        return this.board;
    }

    public EffectHandler getEffectHandler() {

        return this.effectHandler;
    }

    public Player addPlayer(String playerId, String character) throws LoginException, ColorException {

        Color color = Color.ofCharacter(character);

        if (this.playerList.stream()
                .anyMatch(x -> x.getColor().equals(color))) {

            throw new LoginException("Il personaggio selezionato è già stato preso.");
        }

        Player player = new Player(playerId, color);

        this.playerList.add(player);

        return player;
    }

    public Player searchPlayer(String name) throws ColorException {

        Color color = Color.ofCharacter(name);

        for (Player player: this.playerList) {

            if (player.getColor().equals(color)) {

                return player;
            }
        }
        throw new ColorException("Il personaggio che hai scelto non sta giocando.");
    }

    public Player getActivePlayer() {

        return this.activePlayer;
    }

    public synchronized void nextPlayer() {

        if (this.activePlayer != null) {

            this.activePlayer.setActivePlayer(false);

        }

        if (this.activePlayer == null) {

            this.playerList.get(0).setFirstPlayer(true);
            this.activePlayer = this.playerList.get(0);

        } else if (this.playerList.indexOf(this.activePlayer) == this.playerList.size() - 1) {

            this.activePlayer = this.playerList.get(0);

        } else {

            this.activePlayer = this.playerList.get(this.playerList.indexOf(this.activePlayer) + 1);
        }

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

        PointHandler.checkIfDead(this.playerList);
        PointHandler.countKills(this.deaths, this.playerList);

        if (PointHandler.checkEndGame(this.deaths, this.playerList)) {

            throw new EndGameException(PointHandler.endGame(this.deaths, this.playerList));
        }

        this.board.fillBoard();
    }
    
    public JsonObjectBuilder toJsonObject() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        this.playerList.stream()
                .map(Player::toJsonObject)
                .forEach(builder::add);

        return Json.createObjectBuilder()
                .add("numberOfDeaths", this.deaths.getNumberOfDeaths())
                .add("frenzy", this.deaths.isFrenzy())
                .add("playerList", builder.build())
                .add("board", this.board != null ? this.board.toJsonObject() : JsonValue.NULL);
    }
}
