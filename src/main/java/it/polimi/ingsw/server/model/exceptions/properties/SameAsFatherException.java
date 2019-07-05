package it.polimi.ingsw.server.model.exceptions.properties;

/**
 * It is thrown when a target of an optional effect should have been the same target of the base
 * effect but it's not.
 */
public class SameAsFatherException extends PropertiesException {

    /**
     * Creates the exception.
     *
     * @param message The related message.
     */
    public SameAsFatherException(String message) {

        super(message);
    }

}