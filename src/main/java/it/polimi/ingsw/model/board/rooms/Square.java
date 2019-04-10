package it.polimi.ingsw.model.board.rooms;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Square implements Target {

    private int squareId;
    private Room myRoom;

    private EnumMap<Direction, Square> adjacent = new EnumMap<>(Direction.class);
    private EnumMap<Direction, Connection> connection = new EnumMap<>(Direction.class);

    private List<Player> players = new ArrayList<>();

    public Square(Room myRoom, int squareId) {

        this.myRoom = myRoom;
        this.squareId = squareId;
    }

    public int getSquareId() {

        return this.squareId;
    }

    public Room getMyRoom() {

        return this.myRoom;
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

    public List<Player> getPlayers() {

        return this.players;
    }

    public void addPlayer(Player player) {

        this.players.add(player);
        player.setCurrentPosition(this);
    }

    public void removePlayer(Player player) {

        this.players.remove(player);
    }
}
