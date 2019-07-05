package it.polimi.ingsw.server.model.exceptions.properties;

/**
 * It is thrown when there are too many targets in the target list, so the effect cannot be
 * executed.
 */
public class MaxTargetsException extends PropertiesException {

    /**
     * Creates the exception.
     *
     * @param message The related message.
     */
    public MaxTargetsException(String message) {

        super(message);
    }
}
