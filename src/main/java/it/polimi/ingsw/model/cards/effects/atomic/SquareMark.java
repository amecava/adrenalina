package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.List;
import java.util.stream.Stream;

public class SquareMark implements AtomicEffect {

    @Override
    public void execute(Target source, List<Target> targetList) {

        Stream<Player> target;

        try {
            // Get the targets from squares, cast them to Player and filter the source
            target = targetList.stream()
                    .map(x -> (Square) x)
                    .flatMap(x -> x.getPlayers().stream())
                    .filter(x -> x != source);

            // Execute the square mark atomic effect
            target.forEach(x -> x.markPlayer(((Player) source).getPlayerColor()));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }
}
