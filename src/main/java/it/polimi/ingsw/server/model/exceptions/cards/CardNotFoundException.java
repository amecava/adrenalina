package it.polimi.ingsw.server.model.exceptions.cards;

/**
 * An Exception that is mainly thrown when someone is searching a card that is not present in
 * the place where it is being searched.
 */
public class CardNotFoundException extends CardException {

    /**
     * Creates the exception.
     */
    public CardNotFoundException(String message) {

        super(message);
    }
}