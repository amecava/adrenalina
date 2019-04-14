package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.stream.Stream;

public class SquareMark implements AtomicEffect {

    @Override
    public void execute(Target source, AtomicTarget target) {

        try {
            Stream<Player> targetStream;

            // Get the targets from squares, cast them to Player and filter the source
            targetStream = target.getTargetList().stream()
                    .map(x -> (Square) x)
                    .flatMap(x -> x.getPlayers().stream())
                    .filter(x -> x != source);

            // Execute the square mark atomic effect
            targetStream.forEach(x -> x.markPlayer(((Player) source).getPlayerColor()));

            // Launch exception if cast fails
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }
}
