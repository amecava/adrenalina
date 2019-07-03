package it.polimi.ingsw.server.model.exceptions.cards;

public class CardNotFoundException extends CardException {

    /**
     * An Exception that is mainly thrown when someone is searching a card that is not present in
     * the place where it is being searched.
     */
    public CardNotFoundException(String message) {

        super(message);
    }
}