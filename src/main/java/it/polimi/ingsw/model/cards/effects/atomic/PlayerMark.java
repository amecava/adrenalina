package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class PlayerMark implements AtomicEffect {


    private int quantity;

    public PlayerMark(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public void execute(Target source, List<Target> target) {
        target.stream()
                .map(x -> (Player) x)
                .forEach(x -> x.setMark((Player) source, this.quantity));
    }
}

