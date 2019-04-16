package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.exceptions.effects.EffectTypeException;

public interface AtomicEffect {

    AtomicType getAtomicType();

    void execute(Target source, AtomicTarget target) throws EffectTypeException;
}
