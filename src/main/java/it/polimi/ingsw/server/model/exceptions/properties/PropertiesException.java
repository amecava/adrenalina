package it.polimi.ingsw.server.model.exceptions.properties;

/**
 * An Exception that is related to a violated property.
 */
public class PropertiesException extends Exception {

    /**
     * Creates the exception.
     *
     * @param message The related message that explains which property has been violated and why.
     */
    public PropertiesException(String message) {

        super(message);
    }

}
