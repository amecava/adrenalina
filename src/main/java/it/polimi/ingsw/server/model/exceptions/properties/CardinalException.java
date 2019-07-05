package it.polimi.ingsw.server.model.exceptions.properties;

/**
 * It is thrown when two target that should be in a cardinal directions, are not.
 */
public class CardinalException extends PropertiesException {

    /**
     * Creates the exception.
     * @param message The related message.
     */
    public CardinalException(String message) {

        super(message);
    }

}