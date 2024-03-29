package it.polimi.ingsw.server.model.exceptions.jacop;

/**
 * An exception that is thrown every time something related to the Color class goes wrong: wrong
 * calls, wrong conversions.
 */
public class ColorException extends Exception {

    /**
     * Creates the exception.
     *
     * @param string The related message that explain what went wrong.
     */
    public ColorException(String string) {

        super(string);
    }

}