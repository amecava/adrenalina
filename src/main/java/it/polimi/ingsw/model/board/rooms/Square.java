package it.polimi.ingsw.model.board.rooms;

import it.polimi.ingsw.model.Color;
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


    /*
    public void getInfo(){
        System.out.println("square number: " + this.squareID + "in room: " + this.myRoom.getColor());

        if(northConnection != Connection.ENDMAP)
            System.out.println("north: " + this.north.squareID + "in room: " +  this.north.myRoom.getColor());
        System.out.println(this.northConnection);
        System.out.println();

        if(southConnection != Connection.ENDMAP)
            System.out.println("south: " + this.south.squareID + "in room: " +  this.south.myRoom.getColor());
        System.out.println(this.southConnection);
        System.out.println();

        if(eastConnection != Connection.ENDMAP)
            System.out.println("east: " + this.east.squareID + "in room: " +  this.east.myRoom.getColor());
        System.out.println(this.eastConnection);
        System.out.println();

        if(westConnection != Connection.ENDMAP)
            System.out.println("west: " + this.west.squareID + "in room: " +  this.west.myRoom.getColor());
        System.out.println(this.westConnection);
        System.out.println();
    }
    */

    public void setConnections(Connection northConnection, Connection southConnection,
            Connection eastConnection, Connection westConnection) {
        this.northConnection = northConnection;
        this.southConnection = southConnection;
        this.eastConnection = eastConnection;
        this.westConnection = westConnection;
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
        return northConnection;
    }

    public Connection getSouthConnection() {
        return southConnection;
    }

    public Connection getEastConnection() {
        return eastConnection;
    }

    public Connection getWestConnection() {
        return westConnection;
    }

    public Room getMyRoom() {
        return myRoom;
    }

    public Square getNorth() {
        return north;
    }

    public Square getSouth() {
        return south;
    }

    public Square getEast() {
        return east;
    }

    public Square getWest() {
        return west;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public int getSquareID() {
        return squareID;
    }

}

