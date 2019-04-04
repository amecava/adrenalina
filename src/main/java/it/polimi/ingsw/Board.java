package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private List<Room> roomsList;
    private List<Player> playersList;
    private WeaponDeck weaponDeck;

    public Board() {
        this.roomsList = new ArrayList<Room>();
        this.weaponDeck= new WeaponDeck();

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

}
