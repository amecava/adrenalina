package it.polimi.ingsw.server.model.board.rooms;

import it.polimi.ingsw.server.model.ammo.AmmoTile;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.Target;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.TargetType;
import it.polimi.ingsw.server.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.server.model.players.Player;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Square implements Target {

    private int squareId;
    private Room room;

    private boolean spawn;
    private List<Card> tools;

    private Map<Boolean, HashMap<Square, Integer>> map = new HashMap<>();

    private Map<Direction, Square> adjacent = new EnumMap<>(Direction.class);
    private Map<Direction, Connection> connection = new EnumMap<>(Direction.class);

    private List<Player> players = new ArrayList<>();

    public Square(int squareId, boolean spawn) {

        this.squareId = squareId;

        this.spawn = spawn;
        this.tools = new ArrayList<>();

        this.map.put(true, new HashMap<>());
        this.map.put(false, new HashMap<>());

        this.map.get(true).put(this, 0);
        this.map.get(false).put(this, 0);
    }

    @Override
    public TargetType getTargetType() {

        return TargetType.SQUARE;
    }

    @Override
    public Square getCurrentPosition() {

        return this;
    }

    public int getSquareId() {

        return this.squareId;
    }

    public Room getRoom() {

        return this.room;
    }

    public void setRoom(Room room) {

        this.room = room;
    }

    public boolean isSpawn() {

        return this.spawn;
    }

    public List<Card> getTools() {

        return this.tools;
    }

    public List<Square> getAdjacent() {

        return new ArrayList<>(this.adjacent.values());
    }

    public Square getAdjacent(Direction direction) {

        return this.adjacent.get(direction);
    }

    public void setAdjacent(Direction direction, Square square) {

        this.adjacent.put(direction, square);
    }

    public Connection getConnection(Direction direction) {

        return this.connection.get(direction);
    }

    public boolean setConnection(Direction direction, Connection connection) {

        this.connection.put(direction, connection);

        return !connection.equals(Connection.ENDMAP);
    }

    public Map<Square, Integer> getMap(boolean throughWalls) {

        return this.map.get(throughWalls);
    }

    public List<Player> getPlayers() {

        return this.players;
    }

    public void addPlayer(Player player) {

        this.players.add(player);
    }

    public void removePlayer(Player player) {

        this.players.remove(player);
    }

    public void addTool(Card tool) {

        this.tools.add(tool);
    }

    public AmmoTile collectAmmoTile() throws EmptySquareException {

        if (!this.tools.isEmpty()) {

            return (AmmoTile) this.tools.remove(0);
        }

        throw new EmptySquareException("You already collected everything in this square!");
    }

    public Card collectWeaponCard(int id) throws EmptySquareException {

        return this.tools.remove(this.tools.indexOf(
                this.tools.stream()
                        .map(x -> (WeaponCard) x)
                        .filter(y -> y.getId() == id)
                        .findAny().orElseThrow(() ->
                        new EmptySquareException("The card you selected is not in this square"))));
    }

    public Card collectWeaponCard(WeaponCard playerCard, int squareCardId)
            throws EmptySquareException {

        int index = this.tools.indexOf(
                this.tools.stream()
                        .map(x -> (WeaponCard) x)
                        .filter(y -> y.getId() == squareCardId)
                        .findAny().orElseThrow(() ->
                        new EmptySquareException("The card you selected is not in this square")));

        playerCard.setOwner(null);
        this.tools.add(playerCard);

        return this.tools.remove(index);
    }

    public JsonObject toJsonObject() {

        if (this.spawn) {

            JsonArrayBuilder builder = Json.createArrayBuilder();

            this.tools.stream()
                    .map(Card::toJsonObject)
                    .forEach(builder::add);

            if (this.players.isEmpty()) {

                return Json.createObjectBuilder()
                        .add("color", this.room.getColor().toString())
                        .add("isSpawn", this.spawn)
                        .add("tools", builder.build())
                        .add("north", this.connection.get(Direction.NORTH).toString())
                        .add("south", this.connection.get(Direction.SOUTH).toString())
                        .add("east", this.connection.get(Direction.EAST).toString())
                        .add("west", this.connection.get(Direction.WEST).toString())
                        .build();
            } else {

                return  Json.createObjectBuilder().add("color", this.room.getColor().toString())
                        .add("isSpawn", this.spawn)
                        .add("playersIn", Json.createArrayBuilder(this.players.stream()
                                .map(x -> x.getColor().getCharacter())
                                .map(y -> y.substring(0, 2))
                                .collect(Collectors.toList())))
                        .add("cards", builder.build())
                        .add("north", this.connection.get(Direction.NORTH).toString())
                        .add("south", this.connection.get(Direction.SOUTH).toString())
                        .add("east", this.connection.get(Direction.EAST).toString())
                        .add("west", this.connection.get(Direction.WEST).toString())
                        .build();
            }
        } else {

            if (this.players.isEmpty()) {

                return Json.createObjectBuilder().add("color", this.room.getColor().toString())
                        .add("isSpawn", this.spawn)
                        .add("tile", this.tools.get(0).toJsonObject())
                        .add("north", this.connection.get(Direction.NORTH).toString())
                        .add("south", this.connection.get(Direction.SOUTH).toString())
                        .add("east", this.connection.get(Direction.EAST).toString())
                        .add("west", this.connection.get(Direction.WEST).toString())
                        .build();
            } else {

                return Json.createObjectBuilder().add("color", this.room.getColor().toString())
                        .add("isSpawn", this.spawn)
                        .add("playersIn", this.players.isEmpty() ? "empty" :
                                this.players.stream()
                                        .map(x -> x.getColor().getCharacter())
                                        .map(y -> y.substring(0, 2))
                                        .collect(Collectors.joining(" ")))
                        .add("tile", this.tools.get(0).toJsonObject())
                        .add("north", this.connection.get(Direction.NORTH).toString())
                        .add("south", this.connection.get(Direction.SOUTH).toString())
                        .add("east", this.connection.get(Direction.EAST).toString())
                        .add("west", this.connection.get(Direction.WEST).toString())
                        .build();
            }
        }
    }
}
