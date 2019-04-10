package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.List;
import java.util.stream.Stream;

public class SquareDamage implements AtomicEffect {

    private int quantity;

    public SquareDamage(int quantity) {

        this.quantity = quantity;
    }

    @Override
    public void execute(Target source, List<Target> targetList) {

        Stream<Player> target;

        try {
            // Get the targets from squares, cast them to Player.class and filter the source
            target = targetList.stream()
                    .map(x -> (Square) x)
                    .flatMap(x -> x.getPlayers().stream())
                    .filter(x -> x != source);

            // Execute the square damage atomic effect
            target.forEach(x -> x.setDamage(((Player) source).getPlayerColor(), this.quantity));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }
}
