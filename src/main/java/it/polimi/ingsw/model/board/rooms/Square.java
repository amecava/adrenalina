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
    }


    public void addPlayer(Player player) {

        this.players.add(player);
        player.setCurrentPosition(this);
    }

    public void removePlayer(Player player) {

        this.players.remove(player);
    }

    public Connection setNorthConnection(String northConnection) {

        this.northConnection = Connection.valueOf(northConnection);

        return this.northConnection;
    }

    public Connection setSouthConnection(String southConnection) {

        this.southConnection = Connection.valueOf(southConnection);

        return this.southConnection;
    }

    public Connection setEastConnection(String eastConnection) {

        this.eastConnection = Connection.valueOf(eastConnection);

        return this.eastConnection;
    }

    public Connection setWestConnection(String westConnection) {

        this.westConnection = Connection.valueOf(westConnection);

        return this.westConnection;
    }

    public void setNorth(Square north) {

        this.north = north;
    }

    public void setSouth(Square south) {

        this.south = south;
    }

    public void setEast(Square east) {

        this.east = east;
    }

    public void setWest(Square west) {

        this.west = west;
    }

    public Connection getNorthConnection() {

        return this.northConnection;
    }

    public Connection getSouthConnection() {

        return this.southConnection;
    }

    public Connection getEastConnection() {

        return this.eastConnection;
    }

    public Connection getWestConnection() {

        return this.westConnection;
    }

    public Room getMyRoom() {

        return this.myRoom;
    }

    public Square getNorth() {

        return this.north;
    }

    public Square getSouth() {

        return this.south;
    }

    public Square getEast() {

        return this.east;
    }

    public Square getWest() {

        return this.west;
    }

    public List<Player> getPlayers() {

        return this.players;
    }

    public int getSquareID() {

        return this.squareID;
    }

}

