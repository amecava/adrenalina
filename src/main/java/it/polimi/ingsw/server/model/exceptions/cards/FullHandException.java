package it.polimi.ingsw.server.model.exceptions.cards;

/**
 * It is thrown when the player's trying to collect a card without discarding one and he already
 * have three cards in his hand
 */
public class FullHandException extends CardException {

    /**
     * Creates the exception.
     *
     * @param message The related message.
     */
    public FullHandException(String message) {

        super(message);
    }

}
