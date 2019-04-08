package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.decks.WeaponDeck;
import it.polimi.ingsw.model.board.rooms.Connection;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;

public class Board {

    private List<Room> roomsList;
    private WeaponDeck weaponDeck;

    public Board() {
        this.roomsList = new ArrayList<>();
    }

    public void setRoomsList(List<Room> roomsList) {
        this.roomsList = roomsList;
    }


    public List<Room> getRoomsList() {
        return this.roomsList;
    }

    public void giveWeaponCardToPlayer(Player player) throws CardException {
        player.addCardToHand(weaponDeck.getCard());
    }


}
