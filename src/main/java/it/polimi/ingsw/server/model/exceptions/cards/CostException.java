package it.polimi.ingsw.server.model.exceptions.cards;

/**
 * It is thrown either when the reload cost is too high for the player (he doesn't have enough
 * resources to pay) or when the player can't afford the cost of the effect.
 */
public class CostException extends CardException {

    /**
     * Creates the exception.
     * @param message The related message.
     */
    public CostException(String message) {

        super(message);
    }
}