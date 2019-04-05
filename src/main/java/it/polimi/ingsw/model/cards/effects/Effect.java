package it.polimi.ingsw.model.cards.effects;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicEffect;
import it.polimi.ingsw.model.cards.effects.properties.Properties;
import java.util.ArrayList;
import java.util.List;

public class Effect {

    private int id;
    private int inputQuantity;

    private boolean used;
    private boolean activated;

    private Effect sequence;
    private List<Integer> optionalID;

    private Properties effectProperties;

    private List<AtomicEffect> atomicEffectList;

    public int getID() {
        return id;
    }

    public int getInputQuantity() {
        return inputQuantity;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Effect getSequence() {
        return sequence;
    }

    public List<Integer> getOptionalID() {
        return optionalID;
    }

    public Properties getEffectProperties() {
        return effectProperties;
    }

    public Effect(EffectBuilder builder) {
        this.id = builder.id;
        this.inputQuantity = builder.inputQuantity;
        this.used = builder.used;
        this.activated = builder.activated;
        this.sequence = builder.sequence;
        this.optionalID = builder.optionalID;
        this.effectProperties = builder.effectProperties;
        this.atomicEffectList = builder.atomicEffectList;
    }

    //Builder Class
    public static class EffectBuilder {

        private int id;
        private int inputQuantity;

        private boolean used = false;
        private boolean activated = true;

        private Effect sequence;
        private List<Integer> optionalID = new ArrayList<>();

        private Properties effectProperties;

        private List<AtomicEffect> atomicEffectList = new ArrayList<>();

        public EffectBuilder(int id, int inputQuantity, Properties effectProperties) {
            this.id = id;
            this.inputQuantity = inputQuantity;
            this.effectProperties = effectProperties;
        }

        public EffectBuilder setSequence(Effect sequence) {
            this.sequence = sequence;
            return this;
        }

        public EffectBuilder appendOptionalID(Integer optionalID) {
            this.optionalID.add(optionalID);
            return this;
        }

        public Effect build() {
            return new Effect(this);
        }
    }

    public void appendAtomicEffect(AtomicEffect atomicEffect) {

        this.atomicEffectList.add(atomicEffect);

    }

    public void execute(Target source, List<Target> target) {

        for (AtomicEffect atomicEffect : this.atomicEffectList) {
            atomicEffect.execute(source, target);
        }

    }
}
