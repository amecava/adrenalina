package it.polimi.ingsw.server.model.exceptions.properties;

/**
 * It is thrown when the target should have been the same of the owner of the card that  is being
 * used but it's not.
 */
public class SameAsPlayerException extends PropertiesException {

    /**
     * Creates the exception.
     *
     * @param message The related message.
     */
    public SameAsPlayerException(String message) {

        super(message);
    }

}