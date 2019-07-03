package it.polimi.ingsw.server.model.cards.effects.atomic;

import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;

public class AtomicMove implements AtomicEffect, Serializable {

    /**
     * Applies moves to the targets in the target parameter by the player "source".
     *
     * @param source The source of the effect.
     * @param target The target(s) that will be moved.
     */
    @Override
    public void execute(Player source, EffectArgument target) {

        target.getTargetList().stream()
                .flatMap(x -> x.getPlayers().stream())
                .forEach(x -> x.movePlayer(target.getDestination()));
    }
}
