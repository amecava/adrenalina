package it.polimi.ingsw;

import java.util.List;

public class Square implements Target{

    private Room myRoom;
    private int squareID;
    private List<Player> players;

    public Square(Room myRoom, int squareID) {
        this.myRoom = myRoom;
        this.squareID = squareID;
    }

    public List<Player> hasPlayer(){
        return this.players;
    }

    public List<Player> playersInSquare() {
        return this.players;
    }
}
