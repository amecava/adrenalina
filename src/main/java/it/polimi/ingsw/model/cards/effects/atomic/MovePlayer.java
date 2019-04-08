package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.List;
import java.util.stream.Stream;

public class MovePlayer implements AtomicEffect {

    public MovePlayer() {
    }

    @Override
    public void execute(Target source, List<Target> targetList) {

        Square destination;
        Stream<Player> target;

        try {
            destination = (Square) targetList.remove(0);

            target = targetList.stream()
                    .map(x -> (Player) x);

            target.forEach(x -> x.movePlayer(destination));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }
}
