package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.exceptions.effects.EffectTypeException;
import it.polimi.ingsw.model.players.Player;
import java.util.stream.Stream;

public class PlayerMark implements AtomicEffect {

    @Override
    public AtomicType getAtomicType() {

        return AtomicType.MARK;
    }

    @Override
    public void execute(Target source, AtomicTarget target) throws EffectTypeException {

        try {
            Stream<Player> targetStream;

            // Cast the targets to Player
            targetStream = target.getTargetList().stream()
                    .map(x -> (Player) x);

            // Execute the player mark atomic effect
            targetStream.forEach(x -> x.markPlayer(((Player) source).getPlayerColor()));

            // Launch exception if cast fails
        } catch (ClassCastException e) {

            throw new EffectTypeException("This is a player type of effect");
        }
    }
}

