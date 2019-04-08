package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.decks.WeaponDeck;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;

public class Board {

    private WeaponDeck weaponDeck;
    private List<Room> roomsList = new ArrayList<>();

    public Board() {
    }

    public List<Room> getRoomsList() {

        return this.roomsList;
    }

    public void setRoomsList(List<Room> roomsList) {

        this.roomsList = roomsList;
    }

    public void giveWeaponCardToPlayer(Player player) throws CardException {

        player.addCardToHand(weaponDeck.getCard());
    }
}
