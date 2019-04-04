package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String playerID;
    private CardHandler cardHandler;
    private List<Card> weaponCards;

    public Player(String playerID) {

        this.playerID = playerID;
        this.weaponCards = new ArrayList<>();
    }

    public void setCardHandler(CardHandler cardHandler) {
        this.cardHandler = cardHandler;
        System.out.println(cardHandler);
    }

    public String getPlayerID() {
        return this.playerID;
    }

    public void addCardToHand(Card card ) throws MaxCardException {
        if (weaponCards.size() < 3) {
            weaponCards.add(card);
            return;
        } else {
            throw new MaxCardException("You already have 3 cards in your hand!!", card );
        }


    }
}
