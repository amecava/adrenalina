package it.polimi.ingsw.model.board.rooms;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;

public class Square implements Target {

    private Square north;
    private Square south;
    private Square east;
    private Square west;
    private Connection northConnection;
    private Connection southConnection;
    private Connection eastConnection;
    private Connection westConnection;
    private Room myRoom;
    private int squareID;
    private List<Player> players = new ArrayList<>();

    public Square(Room myRoom, int squareID) {
        this.myRoom = myRoom;
        this.squareID = squareID;
        this.northConnection = Connection.ENDMAP;
        this.southConnection = Connection.ENDMAP;
        this.eastConnection = Connection.ENDMAP;
        this.westConnection = Connection.ENDMAP;

    }

    public Square setNorth(Square north, Connection connection) {
        this.north = north;
        this.northConnection = connection;
        north.setSouth(this, connection);
        return this;

    }

    public Square setSouth(Square south, Connection connection) {
        this.south = south;
        this.southConnection = connection;
        return this;

    }

    public Square setEast(Square east, Connection connection) {
        this.eastConnection = connection;
        this.east = east;
        return this;

    }

    public Square setwest(Square west, Connection connection) {
        this.westConnection = connection;
        this.west = west;
        return this;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        player.setCurrentPosition(this);

    }

    public int getSquareID() {
        return squareID;
    }

    public void removePlayer(Player player) {

        this.players.remove(player);
    }

    public List<Player> getPlayers() {
        return this.players;
    }

}
