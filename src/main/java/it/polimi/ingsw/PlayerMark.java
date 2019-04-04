package it.polimi.ingsw;

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
                .forEach(x -> x.setMark((Player)source, this.quantity));
    }
}

