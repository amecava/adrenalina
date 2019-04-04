package it.polimi.ingsw;

import java.util.List;

public class RoomDamage implements AtomicEffect {

    private int quantity;

    public RoomDamage(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public void execute(Target source, List<Target> target) {
        target.stream()
                .map(x -> (Room) x)
                .flatMap(x -> x.getPlayers().stream())
                .forEach(x -> x.setDamage((Player) source, this.quantity));
    }
}
