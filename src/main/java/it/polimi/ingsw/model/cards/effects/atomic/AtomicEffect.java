package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.effects.EffectTarget;
import it.polimi.ingsw.model.players.Player;

public interface AtomicEffect {

    AtomicType getAtomicType();

    void execute(Player source, EffectTarget target);
}
