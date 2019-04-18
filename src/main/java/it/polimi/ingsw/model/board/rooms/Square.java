package it.polimi.ingsw.model.board.rooms;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Square implements Target {

    //TODO check if squareId is actually useful
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

    public void addTool(Card tool) {

        this.tools.add(tool);
    }

    public Card collectAmmoTile() {

        return this.tools.isEmpty() ? null : this.tools.remove(0);
    }

    public Card collectWeaponCard(int id) throws EmptySquareException {

        if (this.tools.stream().map(x -> (WeaponCard) x)
                .anyMatch(y -> y.getId() == id)) {

            return this.tools.remove(this.tools.indexOf(
                    this.tools.stream().map(x -> (WeaponCard) x)
                            .filter(y -> y.getId() == id).findAny().get()));
        } else {
            throw new EmptySquareException("The card you selected is not in this square");
        }
    }

    public Card collectWeaponCard(Card playerCard, int squareCardId) throws EmptySquareException {

        if (this.tools.stream().map(x -> (WeaponCard) x)
                .anyMatch(y -> y.getId() == ((WeaponCard)playerCard).getId())) {

            this.tools.add(playerCard);

            return this.tools.remove(this.tools.indexOf(
                    this.tools.stream().map(x -> (WeaponCard) x)
                            .filter(y -> y.getId() == ((WeaponCard)playerCard).getId()).findAny().get()));

        } else {
            throw new EmptySquareException("The card you selected is not in this square");
        }
    }

    public int getSquareId() {

        return this.squareId;
    }

    public boolean isSpawn() {
        return spawn;
    }

    public Room getRoom() {

        return this.room;
    }

    public void setRoom(Room room) {

        this.room = room;
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

    @Override
    public Square getCurrentPosition() {

        return this;
    }
}
