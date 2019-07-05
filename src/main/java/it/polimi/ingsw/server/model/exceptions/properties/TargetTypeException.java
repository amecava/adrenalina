package it.polimi.ingsw.server.model.exceptions.properties;

/**
 * It is thrown when someone's trying to execute an effect on a wrong target type. For example
 * trying to move a square or shooting a room with a weapon that doesn't allow it.
 */
public class TargetTypeException extends PropertiesException {

    /**
     * Creates the exception.
     *
     * @param message The related message.
     */
    public TargetTypeException(String message) {

        super(message);
    }

}