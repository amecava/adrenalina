package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.effects.TargetType;
import it.polimi.ingsw.server.model.players.Player;
import java.util.List;

public interface Target {

    TargetType getTargetType();

    Square getCurrentPosition();

    List<Player> getPlayers();
}
