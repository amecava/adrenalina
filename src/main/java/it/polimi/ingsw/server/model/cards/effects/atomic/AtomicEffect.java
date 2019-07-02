package it.polimi.ingsw.server.model.cards.effects.atomic;

import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.players.Player;

public interface AtomicEffect {

    /**
     * This method will be implemented differently based on which class will implement this
     * interface. It is the method that actually applies damages, marks and move.
     */
    void execute(Player source, EffectArgument target);
}
