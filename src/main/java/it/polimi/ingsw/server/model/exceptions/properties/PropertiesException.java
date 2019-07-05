package it.polimi.ingsw.server.model.exceptions.properties;

public class PropertiesException extends Exception {

    /**
     * An Exception that is related to a violated property.
     *
     * @param message The related message that explains which property has been violated and why.
     */
    public PropertiesException(String message) {

        super(message);
    }

}
