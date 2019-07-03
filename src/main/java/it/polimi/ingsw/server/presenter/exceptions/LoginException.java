package it.polimi.ingsw.server.presenter.exceptions;

public class LoginException extends Exception {

    /**
     * It is thrown when someone tries to take a character that has already been taken by someone.
     * @param message The related message.
     */
    public LoginException(String message) {

        super(message);
    }
}