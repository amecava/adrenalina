package it.polimi.ingsw.server.model.cards.effects.atomic;

import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.players.Player;

public interface AtomicEffect {

    AtomicType getAtomicType();

    void execute(Player source, EffectArgument target);
}
