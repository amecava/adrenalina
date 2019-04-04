package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private List<Room> roomsList;
    private List<Player> playersList;
    private WeaponDeck weaponDeck;

    public Board() {
        this.roomsList = new ArrayList<Room>();
        this.weaponDeck = new WeaponDeck();

    }

    public void addRooms(Room room) {
        this.roomsList.add(room);
    }

    public List<Room> getRoomsList() {
        List<Room> roomsList = this.roomsList;
        return roomsList;
    }

    public void setPlayersList(List<Player> playerList) {
        this.playersList = playerList;
    }

    public void giveWeaponCardToPlayer (Player player) throws MaxCardException{// da gestire eccezione lanciata, nel controller
                player.addCardToHand(weaponDeck.getcard());


    }
    

    public void connectSquares() {
        // Blue room's connections
        this.roomsList.get(0).getSquaresList().get(0)
                .setEast(this.roomsList.get(0).getSquaresList().get(1), Connection.SQUARE)
                .setSouth(this.roomsList.get(1).getSquaresList().get(0), Connection.DOOR);
        this.roomsList.get(0).getSquaresList().get(1)
                .setEast(this.roomsList.get(0).getSquaresList().get(2), Connection.SQUARE)
                .setSouth(this.roomsList.get(1).getSquaresList().get(1), Connection.WALL);
        this.roomsList.get(0).getSquaresList().get(2)
                .setSouth(this.roomsList.get(1).getSquaresList().get(2), Connection.DOOR);
        // Red room's connections
        this.roomsList.get(1).getSquaresList().get(0)
                .setNorth(this.roomsList.get(0).getSquaresList().get(0), Connection.DOOR)
                .setEast(this.roomsList.get(1).getSquaresList().get(1), Connection.SQUARE);
        this.roomsList.get(1).getSquaresList().get(1)
                .setNorth(this.roomsList.get(0).getSquaresList().get(1), Connection.WALL)
                .setEast(this.roomsList.get(1).getSquaresList().get(2), Connection.SQUARE)
                .setSouth(this.roomsList.get(3).getSquaresList().get(0), Connection.DOOR)
                .setwest(this.roomsList.get(1).getSquaresList().get(0), Connection.SQUARE);
        this.roomsList.get(1).getSquaresList().get(2)
                .setwest(this.roomsList.get(1).getSquaresList().get(1), Connection.SQUARE)
                .setNorth(this.roomsList.get(0).getSquaresList().get(2), Connection.DOOR)
                .setSouth(this.roomsList.get(3).getSquaresList().get(1), Connection.WALL)
                .setEast(this.roomsList.get(2).getSquaresList().get(0), Connection.DOOR);
        // Yellow room's connections
        this.roomsList.get(2).getSquaresList().get(0)
                .setwest(this.roomsList.get(1).getSquaresList().get(2), Connection.DOOR)
                .setSouth(this.roomsList.get(2).getSquaresList().get(1), Connection.SQUARE);
        this.roomsList.get(2).getSquaresList().get(1)
                .setNorth(this.roomsList.get(2).getSquaresList().get(0), Connection.SQUARE)
                .setwest(this.roomsList.get(3).getSquaresList().get(1), Connection.DOOR);
        //White room's connections
        this.roomsList.get(3).getSquaresList().get(0)
                .setNorth(this.roomsList.get(1).getSquaresList().get(1), Connection.DOOR)
                .setEast(this.roomsList.get(3).getSquaresList().get(1), Connection.SQUARE);
        this.roomsList.get(3).getSquaresList().get(1)
                .setwest(this.roomsList.get(3).getSquaresList().get(0), Connection.SQUARE)
                .setNorth(this.roomsList.get(1).getSquaresList().get(2), Connection.WALL)
                .setEast(this.roomsList.get(2).getSquaresList().get(1), Connection.DOOR);
    }

}
