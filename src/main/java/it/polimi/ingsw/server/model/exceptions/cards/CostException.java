package it.polimi.ingsw.server.model.exceptions.cards;

public class CostException extends CardException {

    /**
     * It is thrown either when the reload cost is too high for the player (he doesn't have enough
     * resources to pay) or when the player can't afford the cost of the effect.
     * @param message The related message.
     */
    public CostException(String message) {

        super(message);
    }
}