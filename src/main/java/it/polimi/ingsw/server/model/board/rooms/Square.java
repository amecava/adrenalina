package it.polimi.ingsw.server.model.board.rooms;

import it.polimi.ingsw.server.model.ammo.AmmoTile;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.Target;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.TargetType;
import it.polimi.ingsw.server.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * The Square class. A square can be either a spawn square or a normal square, and based on this it
 * has different properties.
 */
public class Square implements Target, Serializable {

    /**
     * An integer to identify a specific square in its room.
     */
    private int squareId;

    /**
     * A reference to the room in which the square is.
     */
    private Room room;

    /**
     * A boolean that says if this is a square in which the players can spawn either in the first
     * turn or after death.
     */
    private boolean spawn;

    /**
     * The list of what's in the square. If the "spawn" boolean is true this is (dynamically) a list
     * of WeaponCard, otherwise it is a list (with a single) AmmoTile.
     */
    private List<Card> tools;

    /**
     * A "memory" of the distance of any other square in the map (built dynamically). The key of the
     * outer map is a Boolean corresponding to the "throughWalls" property. The inner map saves for
     * each square its distance from this square.
     */
    private Map<Boolean, HashMap<Square, Integer>> map = new HashMap<>();

    /**
     * A map that links this to its adjacent square (or null if the connection is "ENDMAP") in every
     * direction.
     */
    private Map<Direction, Square> adjacent = new EnumMap<>(Direction.class);

    /**
     * A map that, for every directions, says which kind of connection this square has.
     */
    private Map<Direction, Connection> connection = new EnumMap<>(Direction.class);

    /**
     * The list of players that are currently in this square.
     */
    private List<Player> players = new ArrayList<>();

    /**
     * Creates the square and initializes the distance map.
     *
     * @param squareId The id of this square.
     * @param spawn The boolean spawn property.
     */
    public Square(int squareId, boolean spawn) {

        this.squareId = squareId;

        this.spawn = spawn;
        this.tools = new ArrayList<>();

        this.map.put(true, new HashMap<>());
        this.map.put(false, new HashMap<>());

        this.map.get(true).put(this, 0);
        this.map.get(false).put(this, 0);
    }

    /**
     * Gets the TargetType value corresponding to the square.
     *
     * @return The TargetType value.
     */
    @Override
    public TargetType getTargetType() {

        return TargetType.SQUARE;
    }

    /**
     * Gets the current position. Implemented from Target.
     *
     * @return This.
     */
    @Override
    public Square getCurrentPosition() {

        return this;
    }

    /**
     * Gets the id of the square.
     *
     * @return The integer
     */
    public int getSquareId() {

        return this.squareId;
    }

    /**
     * Gets the room of this square.
     *
     * @return The Room of this square.
     */
    public Room getRoom() {

        return this.room;
    }

    /**
     * Sets the room to this square.
     *
     * @param room The room of this square.
     */
    public void setRoom(Room room) {

        this.room = room;
    }

    /**
     * Checks if this is a spawn square.
     */
    public boolean isSpawn() {

        return this.spawn;
    }

    public List<Card> getTools() {

        return this.tools;
    }

    /**
     * Builds a list of all adjacent squares.
     *
     * @return The list of all the adjacent squares.
     */
    public List<Square> getAdjacent() {

        return new ArrayList<>(this.adjacent.values());
    }

    /**
     * Gets the adjacent square in a certain direction.
     *
     * @param direction Te given direction.
     * @return The Square object.
     */
    public Square getAdjacent(Direction direction) {

        return this.adjacent.get(direction);
    }

    /**
     * Sets an adjacent square in a given direction.
     *
     * @param direction The direction.
     * @param square The adjacent square in that direction.
     */
    public void setAdjacent(Direction direction, Square square) {

        this.adjacent.put(direction, square);
    }

    /**
     * Gets the connection type in a given direction.
     *
     * @param direction The direction.
     * @return The Connection value.
     */
    public Connection getConnection(Direction direction) {

        return this.connection.get(direction);
    }

    /**
     * Sets a connection type in a given direction.
     *
     * @param direction The direction.
     * @param connection The connection in the given direction.
     * @return A boolean that says if in that direction there is Connection.ENDMAP.
     */
    public boolean setConnection(Direction direction, Connection connection) {

        this.connection.put(direction, connection);

        return !connection.equals(Connection.ENDMAP);
    }

    /**
     * Gets the map of distances given the boolean key.
     *
     * @param throughWalls The boolean gey to get the correct map.
     * @return The map.
     */
    public Map<Square, Integer> getMap(boolean throughWalls) {

        return this.map.get(throughWalls);
    }

    /**
     * Gets the players in the square.
     *
     * @return The List of Player.
     */
    public List<Player> getPlayers() {

        return this.players;
    }

    /**
     * Adds a player to the list.
     *
     * @param player The player.
     */
    public void addPlayer(Player player) {

        this.players.add(player);
    }

    /**
     * When a player moves away from this square, is removed from the list.
     *
     * @param player The moving player.
     */
    public void removePlayer(Player player) {

        this.players.remove(player);
    }

    /**
     * Updates the tools list with new Cards.
     *
     * @param tool The WeaponCard/AmmoTile that will be added.
     */
    public void addTool(Card tool) {

        this.tools.add(tool);
    }

    /**
     * This method does the "collect" action when the player is in a different square from a
     * spawn-square.
     *
     * @return The AmmoTile in the square.
     * @throws EmptySquareException If the Square is empty
     */
    public AmmoTile collectAmmoTile() throws EmptySquareException {

        if (!this.tools.isEmpty()) {

            return (AmmoTile) this.tools.remove(0);
        }

        throw new EmptySquareException("Hai già raccolto le munizioni in questo quadrato.");
    }

    /**
     * Searches in the tools list the card specified by the parameter and, if is able to find it,
     * returns the list of Color corresponding to its cost.
     *
     * @param id The id of the card.
     * @return The List of Color - the cost of the card.
     * @throws EmptySquareException If the Square is empty
     */
    public List<Color> getCostOfCard(int id) throws EmptySquareException {

        return this.tools.stream()
                .map(x -> (WeaponCard) x)
                .filter(y -> y.getId() == id)
                .findAny()
                .orElseThrow(() ->
                        new EmptySquareException(
                                "La carta che vuoi raccogliere non è in questo quadrato."))
                .getReloadCost();
    }

    /**
     * This method does the "collect" action when the player is in a spawn square and he doesn't
     * need to discard a card because he doesn't have three cards in his hand. It throws an
     * exception if the card isn't in this square.
     *
     * @param id The card the player wants to collect.
     * @return The desired card.
     * @throws EmptySquareException If the Square is empty
     */
    public Card collectWeaponCard(int id) throws EmptySquareException {

        return this.tools.remove(this.tools.indexOf(
                this.tools.stream()
                        .map(x -> (WeaponCard) x)
                        .filter(y -> y.getId() == id)
                        .findAny().orElseThrow(() ->
                        new EmptySquareException(
                                "La carta che vuoi raccogliere non è in questo quadrato."))));
    }

    /**
     * This method does the "collect" action when the player is in a spawn square and needs to also
     * discard  one of the three cards in his hand.
     *
     * @param playerCard The WeaponCard that the user chose to discard.
     * @param squareCardId The id of the card that he wants to collect.
     * @return The desired card.
     * @throws EmptySquareException If the Square is empty.
     */
    public Card collectWeaponCard(WeaponCard playerCard, int squareCardId)
            throws EmptySquareException {

        int index = this.tools.indexOf(
                this.tools.stream()
                        .map(x -> (WeaponCard) x)
                        .filter(y -> y.getId() == squareCardId)
                        .findAny().orElseThrow(() ->
                        new EmptySquareException("The card you selected is not in this square")));

        this.tools.add(playerCard);

        return this.tools.remove(index);
    }

    /**
     * It adds every attribute necessary to identify this square to a JsonObject, and it also build
     * two JsonArray for the players in the square and the tools. This method creates a JsonObject
     * containing all the information needed in the View. The said JsonObject will add up to every
     * other JsonObject of every other (necessary) class and will be sent to the view when needed.
     *
     * @return The JsonObject of the square.
     */
    public JsonObject toJsonObject() {

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder toolsBuilder = Json.createArrayBuilder();
        JsonArrayBuilder playersBuilder = Json.createArrayBuilder();

        this.players.stream()
                .map(Player::toJsonObject)
                .forEach(playersBuilder::add);

        this.tools.stream()
                .map(Card::toJsonObject)
                .forEach(toolsBuilder::add);

        return objectBuilder
                .add("color", this.room.getColor().toString())
                .add("squareId", this.squareId)
                .add("isSpawn", this.spawn)
                .add("tools", toolsBuilder.build())
                .add("playersIn",
                        (this.players.isEmpty()) ? JsonValue.NULL : playersBuilder.build())
                .add("eastConnection", this.connection.get(Direction.EAST).toString())
                .build();
    }
}
