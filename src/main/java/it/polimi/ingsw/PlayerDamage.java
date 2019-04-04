package it.polimi.ingsw;

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
