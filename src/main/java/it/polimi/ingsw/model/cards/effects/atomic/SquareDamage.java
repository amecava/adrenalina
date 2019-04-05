package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class SquareDamage implements AtomicEffect {

    private int quantity;

    public SquareDamage(int quantity) {

        this.quantity = quantity;
    }

    @Override
    public void execute(Target source, List<Target> target) {
        target.stream()
                .map(x -> (Square) x)
                .flatMap(x -> x.getPlayers().stream())
                .filter(x -> x != source)
                .forEach(x -> x.setDamage((Player) source, this.quantity));
    }
}
