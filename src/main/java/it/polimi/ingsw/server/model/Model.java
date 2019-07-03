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

    /**
     * The list of the players of the match.
     */
    private List<Player> playerList = new ArrayList<>();

    /**
     * The board of the match.
     */
    private Board board;

    /**
     * The Death Bridge of the board.
     */
    private Deaths deaths;

    /**
     * The EffectHandler of the match.
     */
    private EffectHandler effectHandler = new EffectHandler();

    /**
     * The current activePlayer.
     */
    private Player activePlayer;

    /**
     * Creates the model by setting the properties sent as parameters.
     *
     * @param numberOfDeaths The number of death of the match.
     * @param frenzy If the final frenzy turn will be played.
     */
    public Model(int numberOfDeaths, boolean frenzy) {

        this.deaths = new Deaths(numberOfDeaths);
        this.deaths.setFrenzy(frenzy);
    }

    /**
     * Gets the playersList.
     *
     * @return The list of players.
     */
    public List<Player> getPlayerList() {

        return this.playerList;
    }

    /**
     * Creates the board.
     *
     * @param vote The number of the most voted board (or the randomly chosed one).
     */
    public void createBoard(int vote) {

        this.board = new Board.BoardBuilder(this.effectHandler).build(vote);
    }

    /**
     * Gets the board.
     *
     * @return The Board.
     */
    public Board getBoard() {

        return this.board;
    }

    /**
     * Gets the EffectHandler of the match.
     *
     * @return The EffectHandler.
     */
    public EffectHandler getEffectHandler() {

        return this.effectHandler;
    }

    /**
     * Adds a player to playerList (if possible).
     *
     * @param playerId The id the player chose.
     * @param character The Character chosen by the player.
     * @return The Player Object.
     * @throws LoginException If the character has already been chosen.
     * @throws ColorException If occurs an error during the character/Color conversion.
     */
    public Player addPlayer(String playerId, String character)
            throws LoginException, ColorException {

        Color color = Color.ofCharacter(character);

        if (this.playerList.stream()
                .anyMatch(x -> x.getColor().equals(color))) {

            throw new LoginException("Il personaggio selezionato è già stato preso.");
        }

        Player player = new Player(playerId, color);

        this.playerList.add(player);

        return player;
    }

    /**
     * Searches a Player based on his id.
     *
     * @param name The id of the player.
     * @return The Player object.
     * @throws ColorException If occurs an error during the character/Color conversion.
     */
    public Player searchPlayer(String name) throws ColorException {

        Color color = Color.ofCharacter(name);

        for (Player player : this.playerList) {

            if (player.getColor().equals(color)) {

                return player;
            }
        }

        throw new ColorException("Il personaggio che hai scelto non sta giocando.");
    }

    public Player getActivePlayer() {

        return this.activePlayer;
    }

    /**
     * Furthers the game by setting the active player to the next one of the playersList.
     */
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

        this.effectHandler.setActivePlayer(this.activePlayer);

        if (this.activePlayer.getAdrenalin().equals(Adrenalin.SECONDFRENZY)) {

            this.activePlayer.setRemainingActions(1);

        } else {

            this.activePlayer.setRemainingActions(2);
        }
    }

    /**
     * Performs the "endOfTurn" action, checks if there are players that need to respawn.
     *
     * @throws EndGameException When the game ends.
     */
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

    /**
     * This method creates a JsonObjectBuilder containing all the information needed in the View. To
     * the said JsonObjectBuilder will be added some JsonValues in the GameHandler.toJsonObject()
     * method, and that will be sent to the view when needed.
     *
     * @return The JsonObect containig all the information of this card.
     */
    public JsonObjectBuilder toJsonObject() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        this.playerList.stream()
                .map(Player::toJsonObject)
                .forEach(builder::add);

        return Json.createObjectBuilder()
                .add("numberOfDeaths", this.deaths.getNumberOfDeaths())// no color???
                .add("frenzy", this.deaths.isFrenzy())
                .add("playerList", builder.build())
                .add("board", this.board != null ? this.board.toJsonObject() : JsonValue.NULL)
                .add("deaths", this.deaths.toJsonDeaths());
    }
}
