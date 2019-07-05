package it.polimi.ingsw.server.model.exceptions.cards;

/**
 * It is thrown when the card that the user is trying to use is not loaded.
 */
public class CardNotLoadedException extends CardException {

    /**
     * Creates the exception.
     * @param message The related message.
     */
    public CardNotLoadedException(String message) {

        super(message);
    }

}
