package it.polimi.ingsw.server.model.cards.effects.atomic;


import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;

/**
 * The Mark atomic effect.
 */
public class AtomicMark implements AtomicEffect, Serializable {

    /**
     * Applies one mark to the targets in the target parameter by the player "source".
     *
     * @param source The source of the mark.
     * @param target The target(s) that will be marked.
     */
    @Override
    public void execute(Player source, EffectArgument target) {

        target.getTargetList().stream()
                .flatMap(x -> x.getPlayers().stream())
                .forEach(x -> x.markPlayer(source.getColor()));
    }
}

