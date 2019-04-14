package it.polimi.ingsw.model.cards.effects;

import it.polimi.ingsw.model.ammo.Ammo;
import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicEffect;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicTarget;
import java.util.ArrayList;
import java.util.List;

public class Effect {

    private int id;
    private int args;
    private String name;
    private String description;
    private EffectType effectType;

    private boolean used;

    private Effect next;
    private List<Integer> optionalID;

    private List<Ammo> cost;
    private Boolean activated;

    private Integer maxTargets;
    private List<Boolean> sameAsFather;
    private Boolean sameAsPlayer;
    private Boolean targetView;
    private Boolean seenByActive;

    private Integer minDist;
    private Integer maxDist;
    private boolean cardinal;
    private boolean throughWalls;
    private boolean differentSquares;

    private List<AtomicEffect> atomicEffectList;

    private Effect(EffectBuilder builder) {

        this.id = builder.id;
        this.args = builder.args;
        this.name = builder.name;
        this.description = builder.description;
        this.effectType = builder.effectType;

        this.used = builder.used;

        this.next = builder.next;
        this.optionalID = builder.optionalID;

        this.cost = builder.cost;
        this.activated = builder.activated;

        this.maxTargets = builder.maxTargets;
        this.sameAsFather = builder.sameAsFather;
        this.sameAsPlayer = builder.sameAsPlayer;
        this.targetView = builder.targetView;
        this.seenByActive = builder.seenByActive;

        this.minDist = builder.minDist;
        this.maxDist = builder.maxDist;
        this.cardinal = builder.cardinal;
        this.throughWalls = builder.throughWalls;
        this.differentSquares = builder.differentSquares;

        this.atomicEffectList = builder.atomicEffectList;
    }

    public int getId() {

        return this.id;
    }

    public int getArgs() {

        return this.args;
    }

    public String getName() {

        return this.name;
    }

    public String getDescription() {

        return this.description;
    }

    public EffectType getEffectType() {

        return this.effectType;
    }

    public boolean isUsed() {

        return this.used;
    }

    public void setUsed(boolean used) {

        this.used = used;
    }

    public Effect getNext() {

        return this.next;
    }

    public List<Integer> getOptionalID() {

        return this.optionalID;
    }

    public List<Ammo> getCost() {

        return this.cost;
    }

    public Boolean getActivated() {

        return this.activated;
    }

    public void setActivated(Boolean activated) {

        this.activated = activated;
    }

    public Integer getMaxTargets() {

        return this.maxTargets;
    }

    public List<Boolean> getSameAsFather() {

        return this.sameAsFather;
    }

    public Boolean getSameAsFather(int index) {

        return this.sameAsFather.get(index);
    }

    public Boolean getSameAsPlayer() {

        return this.sameAsPlayer;
    }

    public Boolean getTargetView() {

        return this.targetView;
    }

    public Boolean getSeenByActive() {

        return this.seenByActive;
    }

    public Integer getMinDist() {

        return this.minDist;
    }

    public Integer getMaxDist() {

        return this.maxDist;
    }

    public boolean isCardinal() {

        return this.cardinal;
    }

    public boolean isThroughWalls() {

        return this.throughWalls;
    }

    public boolean isDifferentSquares() {

        return this.differentSquares;
    }

    public List<AtomicEffect> getAtomicEffectList() {

        return this.atomicEffectList;
    }

    public void appendAtomicEffect(AtomicEffect atomicEffect) {

        if (atomicEffect == null) {
            throw new NullPointerException();
        }

        this.atomicEffectList.add(atomicEffect);
    }

    public void execute(Target source, AtomicTarget target) {

        for (AtomicEffect atomicEffect : this.atomicEffectList) {

            atomicEffect.execute(source, target);
        }
    }

    public static class EffectBuilder {

        private int id;
        private int args;
        private String name;
        private String description;
        private EffectType effectType;

        private boolean used = false;

        private Effect next;
        private List<Integer> optionalID = new ArrayList<>();

        private List<Ammo> cost;
        private Boolean activated;

        private Integer maxTargets;
        private List<Boolean> sameAsFather;
        private boolean sameAsPlayer = false;
        private Boolean targetView;
        private Boolean seenByActive;

        private Integer minDist;
        private Integer maxDist;
        private boolean cardinal = false;
        private boolean throughWalls = false;
        private boolean differentSquares = false;

        private List<AtomicEffect> atomicEffectList = new ArrayList<>();

        public EffectBuilder(int id) {

            this.id = id;
        }

        // TODO

        public Effect build() {

            return new Effect(this);
        }
    }
}
