package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Effect {

    private boolean used;

    private Effect sequence;
    private Effect optional;

    private Properties effectProperties;

    private List<AtomicEffect> atomicEffectList;

    public boolean isUsed() {
        return used;
    }

    public Effect getSequence() {
        return sequence;
    }

    public Effect getOptional() {
        return optional;
    }

    public Properties getEffectProperties() {
        return effectProperties;
    }

    public Effect(EffectBuilder builder) {
        this.used = builder.used;
        this.sequence = builder.sequence;
        this.optional = builder.optional;
        this.effectProperties = builder.effectProperties;
        this.atomicEffectList = builder.atomicEffectList;
    }

    //Builder Class
    public static class EffectBuilder {
        private boolean used = false;

        private Effect sequence;
        private Effect optional;

        private Properties effectProperties;

        private List<AtomicEffect> atomicEffectList = new ArrayList<>();

        public EffectBuilder(Properties effectProperties) {
            this.effectProperties = effectProperties;
        }

        public EffectBuilder setSequence(Effect sequence) {
            this.sequence = sequence;
            return this;
        }

        public EffectBuilder setOptional(Effect optional) {
            this.optional = optional;
            return this;
        }

        public Effect build(){
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
