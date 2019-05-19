package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.players.Player;

public interface AtomicEffect {

    AtomicType getAtomicType();

    void execute(Player source, EffectArgument target);
}
