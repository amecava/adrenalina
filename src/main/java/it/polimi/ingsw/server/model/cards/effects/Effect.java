package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.effects.atomic.AtomicEffect;
import it.polimi.ingsw.server.model.cards.effects.atomic.AtomicType;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * This class represents the effect of a Card or an action. An effect can be Primary, Alternative,
 * Optional1 and Optional2. This class has a list of flags needed to allow the EffectHandler to
 * check if a certain effect can be executed or not. It is the core of the logic of properties.
 */
public class Effect implements Serializable {

    /**
     * The id of the effect.
     */
    private int id;

    /**
     * The number of arguments needed to execute this effect.
     */
    private double args;

    /**
     * The name of the effect.
     */
    private String name;

    /**
     * The description of the effect.
     */
    private String description;

    /**
     * The TargetType of the effect.
     */
    private TargetType targetType;

    /**
     * A boolean that says if this effect has been used or is available.
     */
    private boolean used;

    /**
     * A reference to an effect that needs to be applied right after this effect.
     */
    private Effect next;

    /**
     * The list of the ids of optional effects that are consequences of this effect.
     */
    private List<Integer> optionalId;

    /**
     * The list of color that is the cost of this effect.
     */
    private List<Color> cost;

    /**
     * A Boolean that says if this effect has been activated or not.
     */
    private Boolean activated;

    /**
     * The integer that say the maximum number of targets on which this effect can be applied.
     */
    private Integer maxTargets;

    /**
     * A lis tof Boolean that says if the targets of this effect need to be the same of the targets
     * of the effect that have been executed right before.
     */
    private List<Boolean> sameAsFather;

    /**
     * A boolean that says if the target of this effect needs to be the same of the player who wants
     * to execute it.
     */
    private boolean sameAsPlayer;

    /**
     * A Boolean that says if the target of this effect has to be viewable by the player who is
     * executing it.
     */
    private Boolean targetView;

    /**
     * A boolean that says if the target of this effect has to be viewable by the targets in the
     * "active" list of the EffectHandler.
     */
    private boolean seenByActive;

    /**
     * An integer that says which is the minimum distance (in term of squares) that must occur
     * between the source and the target of this effect.
     */
    private Integer minDist;

    /**
     * An integer that says which is the maximum distance (in term of squares) that must occur
     * between the source and the target of this effect.
     */
    private Integer maxDist;

    /**
     * A boolean that say if the target has to be in a cardinal direction with the player.
     */
    private boolean cardinal;

    /**
     * A boolean that says if this effect can be applied ignoring walls.
     */
    private boolean throughWalls;

    /**
     * A boolean that say if the square of the target (a square can be a target itself) and the
     * square of the player has to be different.
     */
    private boolean differentSquares;

    /**
     * The list of atomic effects.
     */
    private List<AtomicEffect> atomicEffectList;

    /**
     * Builds the effect based on the builder.
     *
     * @param builder The builder.
     */
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

    /**
     * Gets the id of the effect.
     *
     * @return The id of the effect.
     */
    public int getId() {

        return this.id;
    }

    /**
     * Gets the args of the effect.
     *
     * @return The args of the effect.
     */
    public double getArgs() {

        return this.args;
    }


    /**
     * Gets the name of the effect.
     *
     * @return The name of the effect.
     */
    public String getName() {

        return this.name;
    }

    /**
     * Gets the name of the effect.
     *
     * @return The name of the effect.
     */
    public String getDescription() {

        return this.description;
    }

    /**
     * Gets the name of the effect.
     *
     * @return The name of the effect.
     */
    public TargetType getTargetType() {

        return this.targetType;
    }

    /**
     * Checks if the effect is used.
     *
     * @return The used property.
     */
    public boolean isUsed() {

        return this.used;
    }

    /**
     * Sets the used property.
     *
     * @param used The used boolean value.
     */
    public void setUsed(boolean used) {

        this.used = used;
    }

    /**
     * Gets the next effect of the effect.
     *
     * @return The next effect of the effect.
     */
    public Effect getNext() {

        return this.next;
    }

    /**
     * Gets the list of id of optional effects of the effect.
     *
     * @return The list of integer.
     */
    public List<Integer> getOptionalId() {

        return this.optionalId;
    }

    /**
     * Gets the cost of the effect.
     *
     * @return The list of color - the cost of the effect.
     */
    public List<Color> getCost() {

        return this.cost;
    }

    /**
     * Gets the activated property of the effect.
     *
     * @return The activated property of the effect.
     */
    public Boolean getActivated() {

        return this.activated;
    }

    /**
     * Sets the activated property.
     *
     * @param activated The activated value.
     */
    public void setActivated(Boolean activated) {

        this.activated = activated;
    }

    /**
     * Gets the maxArgs property of the effect.
     *
     * @return The maxArgs property of the effect.
     */
    public Integer getMaxTargets() {

        return this.maxTargets;
    }

    /**
     * Gets the sameAsFather property of the effect.
     *
     * @return The list of Boolean - the sameAsFather property of the effect.
     */
    public List<Boolean> getSameAsFather() {

        return this.sameAsFather;
    }

    /**
     * Checks the sameAsPlayer property.
     *
     * @return The boolean of the sameAsPlayer property.
     */
    public boolean isSameAsPlayer() {

        return this.sameAsPlayer;
    }

    /**
     * Gets the targetView property of the effect.
     *
     * @return The targetView property of the effect.
     */
    public Boolean getTargetView() {

        return this.targetView;
    }

    /**
     * Checks the seenByActive property.
     *
     * @return The boolean of the seenByActive property.
     */
    public boolean isSeenByActive() {

        return this.seenByActive;
    }

    /**
     * Gets the minDist property of the effect.
     *
     * @return The minDist property of the effect.
     */
    public Integer getMinDist() {

        return this.minDist;
    }

    /**
     * Gets the maxDist property of the effect.
     *
     * @return The maxDist property of the effect.
     */
    public Integer getMaxDist() {

        return this.maxDist;
    }

    /**
     * Checks the cardinal property.
     *
     * @return The boolean of the cardinal property.
     */
    public boolean isCardinal() {

        return this.cardinal;
    }

    /**
     * Checks the throughWalls property.
     *
     * @return The boolean of the throughWalls property.
     */
    public boolean isThroughWalls() {

        return this.throughWalls;
    }

    /**
     * Checks the differentSquares property.
     *
     * @return The boolean of the differentSquares property.
     */
    public boolean isDifferentSquares() {

        return this.differentSquares;
    }

    /**
     * Executes the effect.
     *
     * @param source The player who wants to execute this effect.
     * @param target The targets on which the player who wants to execute this effect.
     */
    public void execute(Player source, EffectArgument target) {

        for (AtomicEffect atomicEffect : this.atomicEffectList) {

            atomicEffect.execute(source, target);
        }
    }

    /**
     * This method creates a JsonObject containing all the information needed in the View. The said
     * JsonObject will add up to every other JsonObject of every other (necessary) class and will be
     * sent to the view when needed.
     */
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("name", this.name)
                .add("description", this.description)
                .add("cost",
                        this.cost.stream().map(Color::toString).collect(Collectors.joining(" ")))
                .build();
    }

    public static class EffectBuilder {

        private JsonObject jEffectObject;

        private int id;
        private double args;
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

        /**
         * Builds the effect reading the information in the JsonObject "jEffectObject.
         *
         * @return The effect built.
         */
        public Effect build() {

            if (this.jEffectObject.containsKey("id")) {
                this.id = this.jEffectObject.getInt("id");
            }

            if (this.jEffectObject.containsKey("args")) {
                this.args = this.jEffectObject.getJsonNumber("args").doubleValue();
            }

            if (this.jEffectObject.containsKey("name")) {
                this.name = this.jEffectObject.getString("name");
            }

            if (this.jEffectObject.containsKey("description")) {
                this.description = this.jEffectObject.getString("description");
            }

            if (this.jEffectObject.containsKey("targetType")) {
                this.targetType = TargetType.valueOf(this.jEffectObject.getString("targetType"));
            }

            if (this.jEffectObject.containsKey("next")) {
                this.next = new Effect.EffectBuilder(this.jEffectObject.getJsonObject("next"))
                        .build();
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
