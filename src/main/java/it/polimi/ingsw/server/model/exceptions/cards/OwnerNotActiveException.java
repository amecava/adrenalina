package it.polimi.ingsw.server.model.exceptions.cards;

/**
 * It is thrown when the owner of a card is not the active player and is trying to use it.
 */
public class OwnerNotActiveException extends CardException {

    /**
     * Creates the exception.
     * @param message The related message.
     */
    public OwnerNotActiveException(String message) {

        super(message);
    }

}