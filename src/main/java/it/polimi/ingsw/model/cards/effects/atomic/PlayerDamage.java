package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class PlayerDamage implements AtomicEffect {

    private int quantity;

    public PlayerDamage(int quantity) {

        this.quantity = quantity;
    }

    @Override
    public void execute(Target source, List<Target> target) {
        target.stream()
                .map(x -> (Player) x)
                .forEach(x -> x.setDamage((Player) source, this.quantity));
    }
}
