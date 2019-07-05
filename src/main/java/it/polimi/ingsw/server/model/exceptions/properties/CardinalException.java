package it.polimi.ingsw.server.model.exceptions.properties;

public class CardinalException extends PropertiesException {

    /**
     * It is thrown when two target that should be in a cardinal directions, are not.
     * @param message The related message.
     */
    public CardinalException(String message) {

        super(message);
    }

}