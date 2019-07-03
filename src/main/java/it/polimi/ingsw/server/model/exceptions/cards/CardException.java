package it.polimi.ingsw.server.model.exceptions.cards;

public class CardException extends Exception {

    /**
     * An Exception related to the Card class.
     *
     * @param message The message of the exception: will be printed in the cli/shown as a
     * notification in the gui or caught and processed in the GameHandler.
     */
    public CardException(String message) {

        super(message);
    }

}
