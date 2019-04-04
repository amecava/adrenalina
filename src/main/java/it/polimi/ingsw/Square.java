package it.polimi.ingsw;

import java.util.List;

public class Square implements Target{
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
    private List<Player> players;

    public Square(Room myRoom, int squareID) {
        this.myRoom = myRoom;
        this.squareID = squareID;
        this.north = null;
        this.south = null;
        this.east = null;
        this.west = null;
        this.northConnection=Connection.ENDMAP;
        this.southConnection=Connection.ENDMAP;
        this.eastConnection=Connection.ENDMAP;
        this.westConnection=Connection.ENDMAP;

    }

    public Square setNorth(Square north, Connection connection) {
        this.north = north;
        this.northConnection=connection;
        return this;

    }
    public Square setSouth(Square south, Connection connection) {
        this.south = south;
        this.southConnection=connection;
        return this;

    }
    public Square setEast (Square east, Connection connection){
        this.eastConnection=connection;
        this.east=east;
        return  this;

    }
    public Square setwest(Square west, Connection connection){
        this.westConnection=connection;
        this.west=west;
        return this;
    }

    public void addPlayer(Player player) {
        this.players.add(player);

    }

    public List<Player> playersInSquare() {
        return this.players;
    }

    public List<Player> playersInSquare() {
        return this.players;
    }
}
