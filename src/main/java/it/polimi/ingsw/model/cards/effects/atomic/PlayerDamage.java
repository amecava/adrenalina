package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.players.Player;
import java.util.stream.Stream;

public class PlayerDamage implements AtomicEffect {

    @Override
    public void execute(Target source, AtomicTarget target) {

        try {
            Stream<Player> targetStream;

            // Cast the targets to Player
            targetStream = target.getTargetList().stream()
                    .map(x -> (Player) x);

            // Execute the player damage atomic effect
            targetStream.forEach(x -> x.damagePlayer(((Player) source).getPlayerColor()));

            // Launch exception if cast fails
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
    }
}
