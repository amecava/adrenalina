package it.polimi.ingsw.model.exceptions.jacop;

import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class EndGameException extends Throwable {

    List<List<Player>> winner;

    public EndGameException(List<List<Player>> winner) {

        this.winner = winner;
    }

    public List<List<Player>> getWinner() {

        return this.winner;
    }
}
