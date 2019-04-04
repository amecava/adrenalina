package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerID;
    private CardHandler cardHandler;
    private List<Card> weaponsDeck;

    public Player(String playerID) {

        this.playerID = playerID;
        this.weaponsDeck = new ArrayList<Card>();
    }

    public void setCardHandler(CardHandler cardHandler){
        this.cardHandler = cardHandler;
        System.out.println(cardHandler);
    }

    public String getPlayerID() {
        return playerID;
    }

    public void addCardToDeck(Card card){
        this.weaponsDeck.add(card);
    }
}
