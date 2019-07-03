package it.polimi.ingsw.server.presenter.exceptions;

public class SpawnException extends Exception {

    /**
     * It is thrown when someone is trying to spawn and something goes wrong.
     * @param message The related message.
     */
    public SpawnException(String message) {

        super(message);
    }
}
