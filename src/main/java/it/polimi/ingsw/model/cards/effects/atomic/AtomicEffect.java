package it.polimi.ingsw.model.cards.effects.atomic;

import it.polimi.ingsw.model.cards.Target;
import java.util.List;

public interface AtomicEffect {

    void execute(Target source, AtomicTarget target);
}
