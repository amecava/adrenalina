package it.polimi.ingsw;

import java.util.List;

public class MovePlayer implements AtomicEffect {

    public MovePlayer() {
    }

    @Override
    public void execute(Target destination, List<Target> target) {
        target.stream().map(x -> (Player) x).forEach(x -> x.movePlayer((Square) destination));
        for (Target targets : target ){
            ((Square) destination).addPlayer((Player) targets);
            ((Player)targets).getCurrentPosition().removePlayer((Player)targets);
            ((Player)targets).setCurrentPosition((Square)destination);

        }
    }
}
