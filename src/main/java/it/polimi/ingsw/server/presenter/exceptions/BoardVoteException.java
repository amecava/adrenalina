package it.polimi.ingsw.server.presenter.exceptions;

public class BoardVoteException extends Exception {

    /**
     * It is thrown when someone who already voted the board tries to vote again.
     * @param message The related message.
     */
    public BoardVoteException(String message) {

        super(message);
    }
}
