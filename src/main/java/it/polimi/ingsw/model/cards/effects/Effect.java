package it.polimi.ingsw.model.cards.effects;

import it.polimi.ingsw.model.cards.Target;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicEffect;
import it.polimi.ingsw.model.cards.effects.properties.Properties;
import java.util.ArrayList;
import java.util.List;

public class Effect {

    private int id;
    private int args;
    private String name;
    private EffectType effectType;

    private boolean used;

    private Effect next;
    private List<Integer> optionalID;

    private Properties effectProperties;

    private List<AtomicEffect> atomicEffectList;

    private String description;

    public int getId() {

        return this.id;
    }

    public int getArgs() {

        return this.args;
    }

    public String getName() {

        return this.name;
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

    public Properties getEffectProperties() {

        return this.effectProperties;
    }

    public List<AtomicEffect> getAtomicEffectList() {

        return this.atomicEffectList;
    }

    public String getDescription() {

        return this.description;
    }

    private Effect(EffectBuilder builder) {

        this.id = builder.id;
        this.args = builder.args;
        this.name = builder.name;
        this.effectType = builder.effectType;

        this.used = builder.used;

        this.next = builder.next;
        this.optionalID = builder.optionalID;

        this.effectProperties = builder.effectProperties;

        this.atomicEffectList = builder.atomicEffectList;

        this.description = builder.description;
    }

    public void appendAtomicEffect(AtomicEffect atomicEffect) {

        if (atomicEffect == null) {
            throw new NullPointerException();
        }

        this.atomicEffectList.add(atomicEffect);

    }

    public void execute(Target source, List<Target> target) {

        for (AtomicEffect atomicEffect : this.atomicEffectList) {
            atomicEffect.execute(source, target);
        }
    }

    public static class EffectBuilder {

        private int id;
        private int args;
        private String name;
        private EffectType effectType;

        private boolean used = false;

        private Effect next;
        private List<Integer> optionalID = new ArrayList<>();

        private Properties effectProperties;

        private List<AtomicEffect> atomicEffectList = new ArrayList<>();

        private String description;

        public EffectBuilder(int id, int args, String name, EffectType effectType,
                Properties effectProperties, String description) {

            this.id = id;
            this.args = args;
            this.name = name;
            this.effectType = effectType;

            this.effectProperties = effectProperties;

            this.description = description;
        }

        public EffectBuilder setNext(Effect next) {

            this.next = next;
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
}
