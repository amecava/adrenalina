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

    public void addRoomsList(Room room) {
        this.roomsList.add(room);
    }

    public void setPlayersList(List<Player> playerList) {
        this.playersList = playerList;
    }

    public Card  giveWeaponCardToPlayer (Player player){
           try {
                player.addCardToHand(weaponDeck.getcard());
           }
    }

}
