package it.polimi.ingsw.server.model.exceptions.cards;

public class CardNotLoadedException extends CardException {

    /**
     * It is thrown when the card that the user is trying to use is not loaded.
     * @param message The related message.
     */
    public CardNotLoadedException(String message) {

        super(message);
    }

}
