package it.polimi.ingsw.server.model.exceptions.cards;

public class EmptySquareException extends CardException {

    /**
     * It is thrown when someone's searching a Card in a Square that is empty.
     * @param message The related message.
     */
    public EmptySquareException(String message) {

        super(message);
    }
}

