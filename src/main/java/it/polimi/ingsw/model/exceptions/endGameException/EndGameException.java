package it.polimi.ingsw.model.exceptions.endGameException;

import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class EndGameException extends Throwable {
    String description ;
    List<List<Player>> winner;
    public EndGameException(String description, List<List<Player>> winner){
        this.description=description;
        this.winner=winner;
    }

}
