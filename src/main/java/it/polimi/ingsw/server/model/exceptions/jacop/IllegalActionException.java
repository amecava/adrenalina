package it.polimi.ingsw.server.model.exceptions.jacop;

public class IllegalActionException extends Exception {

    /**
     * An exception that is thrown every time someone is trying to perform an action he is not
     * allowed to.
     *
     * @param string The related message explaining why he can't do what he's doing.
     */
    public IllegalActionException(String string) {

        super(string);
    }

}
