package it.polimi.ingsw.server.model.exceptions.properties;

public class DuplicateException extends PropertiesException {

    /**
     * It is thrown when there are too many targets in the target list, so the effect cannot be
     * executed.
     *
     * @param message The related message.
     */
    public DuplicateException(String message) {

        super(message);
    }
}
