package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.players.Player;
import java.util.List;

public class MovePlayer implements AtomicEffect {

    private Square destination;

    public MovePlayer() {
    }

    @Override
    public void execute(Target source, List<Target> target) {
        this.destination = (Square) target.remove(0);

        target.stream().map(x -> (Player) x).forEach(x -> x.movePlayer(destination));
    }
}
