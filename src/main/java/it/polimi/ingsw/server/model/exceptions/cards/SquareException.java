package it.polimi.ingsw.server.model.exceptions.cards;

public class SquareException extends CardException {

    /**
     * It is thrown when the player's trying to collect in the wrong square. For example s trying to
     * collect an ammoTile while being on a spawn square.
     *
     * @param message The related message.
     */
    public SquareException(String message) {

        super(message);
    }

}

