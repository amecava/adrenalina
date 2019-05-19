package it.polimi.ingsw.server.model.exceptions.cards;

public class CardNotFoundException extends CardException {

    public CardNotFoundException(String message) {

        super(message);
    }
}