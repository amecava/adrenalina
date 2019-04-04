package it.polimi.ingsw;

import java.util.List;

public class MovePlayer implements AtomicEffect {

    public MovePlayer() {
    }

    @Override
    public void execute(Target destination, List<Target> target) {
        target.stream().map(x -> (Player) x).forEach(x -> x.movePlayer((Square) destination));
    }
}
