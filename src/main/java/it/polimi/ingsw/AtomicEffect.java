package it.polimi.ingsw;

import java.util.List;

public interface AtomicEffect {

    public abstract void execute(Target source, List<Target> target);

}
