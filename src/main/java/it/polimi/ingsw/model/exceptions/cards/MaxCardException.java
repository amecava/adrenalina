package it.polimi.ingsw.model.exceptions.cards;

import it.polimi.ingsw.model.cards.Card;

public class MaxCardException extends CardException {

    private Card card;

    public MaxCardException(String message, Card card) {

        super(message);
        this.card = card;
    }

    public Card getCard() {

        return this.card;
    }
}
