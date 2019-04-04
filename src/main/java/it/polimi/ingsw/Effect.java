package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Effect {

    private List<AtomicEffect> atomicEffectsList;
    private Effect optional;
    private int quantity;
    private int minDist;
    private int maxDist;
    private int maxPlayers;
    private boolean targetView;
    private boolean sameAsFather;
    private boolean seenByActive;

    public Effect() {
        this.atomicEffectsList = new ArrayList<>();
        this.quantity = 0;
        this.minDist = 10;
        this.maxDist = 0;
        this.maxPlayers = 0;
        this.targetView = false;
        this.sameAsFather = false;
        this.seenByActive = false;
    }

    public void addAtomicEffects(AtomicEffect a){
        final boolean added = this.atomicEffectsList.add(a);
    }
}
