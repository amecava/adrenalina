package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private List<Square> squaresList;
    private String color;
    private List<Player> playersInRoom;

    public Room(String color) {
        this.squaresList = new ArrayList<Square>();
        this.color = color;
        this.playersInRoom = new ArrayList<>();
    }

    public void addSquaresList(List<Square> squaresList) {
        this.squaresList = squaresList;

    }

    public List<Player> getPlayers(){
        this.playersInRoom.clear(); // WARNING
        for(Square s: squaresList){
            this.playersInRoom.addAll(s.hasPlayer());
        }
        return this.playersInRoom;
    }
}
