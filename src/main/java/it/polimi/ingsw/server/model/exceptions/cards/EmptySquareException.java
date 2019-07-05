package it.polimi.ingsw.server.model.exceptions.cards;

/**
 * It is thrown when someone's searching a Card in a Square that is empty.
 */
public class EmptySquareException extends CardException {

    /**
     * Creates the exception.
     * @param message The related message.
     */
    public EmptySquareException(String message) {

        super(message);
    }
}

