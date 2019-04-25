package it.polimi.ingsw.model.cards.effects;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicEffect;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicType;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonObject;

public class Effect {

    private int id;
    private int args;
    private String name;
    private String description;
    private TargetType targetType;

    private boolean used;

    private Effect next;
    private List<Integer> optionalId;

    private List<Color> cost;
    private Boolean activated;

    private Integer maxTargets;
    private List<Boolean> sameAsFather;
    private boolean sameAsPlayer;
    private Boolean targetView;
    private boolean seenByActive;

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
        this.targetType = builder.targetType;

        this.used = builder.used;

        this.next = builder.next;
        this.optionalId = builder.optionalId;

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

    public TargetType getTargetType() {

        return this.targetType;
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

    public List<Integer> getOptionalId() {

        return this.optionalId;
    }

    public List<Color> getCost() {

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

    public boolean isSameAsPlayer() {

        return this.sameAsPlayer;
    }

    public Boolean getTargetView() {

        return this.targetView;
    }

    public boolean isSeenByActive() {

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

    public void execute(Player source, EffectTarget target) {

        for (AtomicEffect atomicEffect : this.atomicEffectList) {

            atomicEffect.execute(source, target);
        }
    }

    public static class EffectBuilder {

        private JsonObject jEffectObject;

        private int id;
        private int args;
        private String name;
        private String description;
        private TargetType targetType;

        private boolean used = false;

        private Effect next;
        private List<Integer> optionalId = new ArrayList<>();

        private List<Color> cost = new ArrayList<>();
        private Boolean activated;

        private Integer maxTargets;
        private List<Boolean> sameAsFather = new ArrayList<>();
        private boolean sameAsPlayer = false;
        private Boolean targetView;
        private boolean seenByActive = false;

        private Integer minDist;
        private Integer maxDist;
        private boolean cardinal = false;
        private boolean throughWalls = false;
        private boolean differentSquares = false;

        private List<AtomicEffect> atomicEffectList = new ArrayList<>();

        public EffectBuilder(JsonObject jEffectObject) {

            this.jEffectObject = jEffectObject;
        }

        public Effect build() {

            this.id = this.jEffectObject.getInt("id");

            if (this.jEffectObject.containsKey("args")) {
                this.args = this.jEffectObject.getInt("args");
            }

            if (this.jEffectObject.containsKey("name")) {
                this.name = this.jEffectObject.getString("name");
            }

            if (this.jEffectObject.containsKey("description")) {
                this.description = this.jEffectObject.getString("description");
            }

            this.targetType = TargetType.valueOf(this.jEffectObject.getString("targetType"));

            if (this.jEffectObject.containsKey("next")) {
                this.next = new Effect.EffectBuilder(this.jEffectObject.getJsonObject("next")).build();
            }

            if (this.jEffectObject.containsKey("optionalId")) {
                this.jEffectObject.getJsonArray("optionalId")
                        .forEach(x -> this.optionalId.add(Integer.parseInt(x.toString())));
            }

            if (this.jEffectObject.containsKey("cost")) {
                this.jEffectObject.getJsonArray("cost")
                        .forEach(x -> this.cost.add(Color.valueOf(
                                x.toString().substring(1, x.toString().length() - 1))));
            }

            if (this.jEffectObject.containsKey("activated")) {
                this.activated = this.jEffectObject.getBoolean("activated");
            }

            if (this.jEffectObject.containsKey("maxTargets")) {
                this.maxTargets = this.jEffectObject.getInt("maxTargets");
            }

            if (this.jEffectObject.containsKey("sameAsFather")) {
                this.jEffectObject.getJsonArray("sameAsFather")
                        .forEach(x -> this.sameAsFather.add(Boolean.valueOf(x.toString())));
            }

            if (this.jEffectObject.containsKey("sameAsPlayer")) {
                this.sameAsPlayer = this.jEffectObject.getBoolean("sameAsPlayer");
            }

            if (this.jEffectObject.containsKey("targetView")) {
                this.targetView = this.jEffectObject.getBoolean("targetView");
            }

            if (this.jEffectObject.containsKey("seenByActive")) {
                this.seenByActive = this.jEffectObject.getBoolean("seenByActive");
            }

            if (this.jEffectObject.containsKey("minDist")) {
                this.minDist = this.jEffectObject.getInt("minDist");
            }

            if (this.jEffectObject.containsKey("maxDist")) {
                this.maxDist = this.jEffectObject.getInt("maxDist");
            }

            if (this.jEffectObject.containsKey("cardinal")) {
                this.cardinal = this.jEffectObject.getBoolean("cardinal");
            }

            if (this.jEffectObject.containsKey("throughWalls")) {
                this.throughWalls = this.jEffectObject.getBoolean("throughWalls");
            }

            if (this.jEffectObject.containsKey("differentSquares")) {
                this.differentSquares = this.jEffectObject.getBoolean("differentSquares");
            }

            if (this.jEffectObject.containsKey("atomicEffectList")) {
                this.jEffectObject.getJsonArray("atomicEffectList").forEach(x ->
                        this.atomicEffectList.add(AtomicType
                                .valueOf(x.toString().substring(1, x.toString().length() - 1))
                                .getAtomicEffect()
                        ));
            }

            return new Effect(this);
        }
    }
}
