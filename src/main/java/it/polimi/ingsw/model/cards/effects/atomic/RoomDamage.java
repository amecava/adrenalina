package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.players.Player;
import java.util.stream.Stream;

public class RoomDamage implements AtomicEffect {

    @Override
    public void execute(Target source, AtomicTarget target) {

        try {
            Stream<Player> targetStream;

            // Get the targets from rooms, cast them to Player and filter the source
            targetStream = target.getTargetList().stream()
                    .map(x -> (Room) x)
                    .flatMap(x -> x.getPlayers().stream())
                    .filter(x -> x != source);

            // Execute the room damage atomic effect
            targetStream.forEach(x -> x.damagePlayer(((Player) source).getPlayerColor()));

            // Launch exception if cast fails
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }
}