package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.cards.effects.EffectType;
import java.util.List;

public interface AtomicEffect {

    AtomicType getAtomicType();

    void execute(Target source, AtomicTarget target);
}
