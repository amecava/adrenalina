package it.polimi.ingsw.server.model.exceptions.jacop;

import it.polimi.ingsw.server.model.players.Player;
import java.util.List;

public class EndGameException extends Exception {

    private final List<List<Player>> winner;

    public EndGameException(List<List<Player>> winner) {

        this.winner = winner;
    }

    public List<List<Player>> getWinner() {

        return this.winner;
    }
}
