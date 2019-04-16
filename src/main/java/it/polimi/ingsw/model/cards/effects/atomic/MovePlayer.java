package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.exceptions.effects.EffectTypeException;
import it.polimi.ingsw.model.players.Player;
import java.util.stream.Stream;

public class MovePlayer implements AtomicEffect {

    @Override
    public AtomicType getAtomicType() {

        return AtomicType.MOVE;
    }

    @Override
    public void execute(Target source, AtomicTarget target) throws EffectTypeException {

        if (target.getDestination() == null) {

            throw new EffectTypeException("Wrong number of arguments to method call!");
        }

        try {
            Stream<Player> targetStream;

            // Cast the targets to Player
            targetStream = target.getTargetList().stream()
                    .map(x -> (Player) x);

            // Execute the move player atomic effect
            targetStream.forEach(x -> x.movePlayer(target.getDestination()));

            // Launch exception if cast fails
        } catch (ClassCastException e) {

            throw new EffectTypeException("This is a player type of effect");
        }
    }
}
