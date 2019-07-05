package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.effects.TargetType;
import it.polimi.ingsw.server.model.players.Player;
import java.util.List;

/**
 * The interface for the targets with basic method implementation for the possible targets of effects.
 */
public interface Target {

    /**
     * Gets the type of a target.
     *
     * @return The type of a target.
     */
    TargetType getTargetType();

    /**
     * Gets the current position of a target (if possible).
     *
     * @return The current position of a target.
     */
    Square getCurrentPosition();

    /**
     * Gets the list of targets of a target (if possible).
     *
     * @return The list of targets of a target.
     */
    List<Player> getPlayers();
}
