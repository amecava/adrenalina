package it.polimi.ingsw;

import java.util.List;

public class SquareMark implements AtomicEffect {

    private int quantity;

    public SquareMark(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public void execute(Target source, List<Target> target) {
        target.stream()
                .map(x -> (Square) x)
                .flatMap(x -> x.playersInSquare().stream())
                .forEach(x -> x.setMark((Player) source, this.quantity));
    }
}
