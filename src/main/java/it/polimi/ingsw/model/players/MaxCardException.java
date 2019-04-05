package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.cards.Card;

public class MaxCardException extends Exception {

    private String information;
    private Card card;

    public MaxCardException(String information, Card card) {
        this.information = information;
        this.card = card;
    }

    public String getInformation() {
        return information;
    }

    public Card getCard() {
        return card;
    }
}
