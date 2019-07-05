package it.polimi.ingsw.server.model.exceptions.jacop;

import it.polimi.ingsw.server.model.players.Player;
import java.util.List;

/**
 * An exception that is thrown only once in the game, when, during the "endOfTurn" method in the
 * gameHandler, the game ends.
 */
public class EndGameException extends Exception {

    /**
     * The List of List of players (two different players can end with the same points), ordered
     * from the one who managed to get more points, decreasing.
     */
    private final List<List<Player>> winner;

    /**
     * Creates the exception.
     *
     * @param winner The ordered list of players.
     */
    public EndGameException(List<List<Player>> winner) {

        this.winner = winner;
    }

    /**
     * Gets the list of players.
     *
     * @return The List of players.
     */
    public List<List<Player>> getWinner() {

        return this.winner;
    }
}
