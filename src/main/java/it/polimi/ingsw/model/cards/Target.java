package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.effects.TargetType;
import it.polimi.ingsw.model.players.Player;
import java.util.List;

public interface Target {

    TargetType getTargetType();

    Square getCurrentPosition();

    List<Player> getPlayers();
}
