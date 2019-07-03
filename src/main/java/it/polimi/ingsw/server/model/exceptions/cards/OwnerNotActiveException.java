package it.polimi.ingsw.server.model.exceptions.cards;

public class OwnerNotActiveException extends CardException {

    /**
     * It is thrown when the owner of a card is not the active player and is trying to use it.
     * @param message The related message.
     */
    public OwnerNotActiveException(String message) {

        super(message);
    }

}