package it.polimi.ingsw.server.model.exceptions.jacop;

/**
 * An exception that is thrown every time someone is trying to perform an action he is not allowed
 * to.
 */
public class IllegalActionException extends Exception {

    /**
     * Creates the exception.
     *
     * @param string The related message explaining why he can't do what he's doing.
     */
    public IllegalActionException(String string) {

        super(string);
    }

}
