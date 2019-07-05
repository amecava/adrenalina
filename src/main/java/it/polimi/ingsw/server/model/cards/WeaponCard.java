package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.ammo.Cost;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.cards.effects.Effect;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.cards.CardNotLoadedException;
import it.polimi.ingsw.server.model.exceptions.cards.CostException;
import it.polimi.ingsw.server.model.exceptions.cards.OwnerNotActiveException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectUsedException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * The weapon card class to model the weapon card and offer methods to execute its effects.
 */
public class WeaponCard implements Card, Serializable {

    /**
     * A reference of the owner of the card (may be null).
     */
    private Player owner;

    /**
     * The EffectHandler of the game.
     */
    private EffectHandler effectHandler;

    /**
     * The id of the Card.
     */
    private int id;

    /**
     * The name of the Weapon.
     */
    private String name;

    /**
     * A boolean that says if the weapon is currently loaded.
     */
    private boolean loaded;

    /**
     * A list of Color, the total cost of the card.
     */
    private List<Color> reloadCost;

    /**
     * A map that links the Effect object to its abstract representation EffectType.
     */
    private EnumMap<EffectType, Effect> map;

    /**
     * Notes that describe the weapon.
     */
    private String notes;

    /**
     * Initializes the WeaponCard based on the builder.
     */
    private WeaponCard(WeaponCardBuilder builder) {

        this.effectHandler = builder.effectHandler;

        this.id = builder.id;

        this.name = builder.name;
        this.loaded = builder.loaded;
        this.reloadCost = builder.reloadCost;

        this.map = builder.map;

        this.notes = builder.notes;
    }

    /**
     * Gets the name of the card.
     *
     * @return The name of the Card.
     */
    @Override
    public String getName() {

        return this.name;
    }

    /**
     * Gets the color of the card.
     *
     * @return The Color of the Card.
     */
    @Override
    public Color getColor() {

        return this.reloadCost.get(0);
    }

    /**
     * Gets the current owner of the card.
     *
     * @return The Player object.
     */
    public Player getOwner() {

        return this.owner;
    }

    /**
     * Sets the current owner of the card.
     *
     * @param owner The Player.
     */
    public void setOwner(Player owner) {

        this.owner = owner;
    }

    /**
     * Gets the id of the card.
     *
     * @return The id of the card.
     */
    public int getId() {

        return this.id;
    }

    /**
     * Gets the boolean that says if the card is loaded.
     *
     * @return The boolean loaded.
     */
    public boolean isLoaded() {

        return this.loaded;
    }

    /**
     * Updates the boolean loaded.
     *
     * @param loaded The new loaded value.
     */
    public void setLoaded(boolean loaded) {

        this.loaded = loaded;
    }

    /**
     * Gets the list of color - the reload cost.
     *
     * @return The list - reload cost.
     */
    public List<Color> getReloadCost() {

        return this.reloadCost;
    }

    /**
     * Gets the map of the EffectType - Effect.
     *
     * @return The Map.
     */
    public Map<EffectType, Effect> getMap() {

        return this.map;
    }

    /**
     * Gets the notes of the Card.
     *
     * @return The string of the notes.
     */
    public String getNotes() {

        return this.notes;
    }

    /**
     * Gets the optional effects of the card (if any).
     *
     * @return The list of optional effects.
     */
    public List<Effect> getOptionalList() {

        List<Effect> optionalList = new ArrayList<>();

        if (map.containsKey(EffectType.OPTIONAL_1)) {

            optionalList.add(map.get(EffectType.OPTIONAL_1));
        }

        if (map.containsKey(EffectType.OPTIONAL_2)) {

            optionalList.add(map.get(EffectType.OPTIONAL_2));
        }

        return optionalList;
    }

    /**
     * Reloads the weapon and updates the owner's ammo cubes list.
     *
     * @param powerUpCardList A list of PowerUpCards that the owner may want to use to reload.
     * @throws IllegalActionException If the card is already loaded.
     * @throws CostException If the owner doesn't have enough resources to pay the reload cost.
     */
    public void reloadWeapon(List<PowerUpCard> powerUpCardList)
            throws IllegalActionException, CostException {

        if (this.isLoaded()) {

            throw new IllegalActionException("La carta è già carica");
        }

        List<Color> costCopy = new ArrayList<>(this.reloadCost);

        Cost.checkCost(this.owner, costCopy, powerUpCardList);

        // Set card loaded flag to true
        this.loaded = true;

        // Set primary effect used flag to false
        this.map.get(EffectType.PRIMARY).setUsed(false);

        // Set alternative effect used flag to false if present
        if (this.map.containsKey(EffectType.ALTERNATIVE)) {

            this.map.get(EffectType.ALTERNATIVE).setUsed(false);
        }

        // Deactivate optionals if they've been activated and set used flag to false
        this.getOptionalList().forEach(x -> {
            x.setUsed(false);

            if (x.getActivated() != null) {
                x.setActivated(false);
            }
        });

        costCopy.forEach(x ->
                this.owner.getAmmoCubesList().stream()
                        .filter(y -> y.getColor().equals(x) && !y.isUsed())
                        .findFirst().get()
                        .setUsed(true)
        );
    }

    /**
     * Activates this card by checking the properties.
     *
     * @return This WeaponCard.
     * @throws CardException If some properties that are necessary in order to activate the card are
     * violated.
     */
    public WeaponCard activateCard() throws CardException {

        // Launch exception if owner of card is not the active player
        if (!this.effectHandler.getActivePlayer().equals(this.owner)) {

            throw new OwnerNotActiveException("Can't use this card right now!");
        }

        // Launch exception if the card is not loaded
        if (!this.loaded) {

            throw new CardNotLoadedException("Weapon not loaded!");
        }

        this.effectHandler.getActive().clear();
        this.effectHandler.getInactive().clear();

        return this;
    }

    /**
     * Performs the "useCard" action, it executes the effect "effectType" to the targets in
     * "target".
     *
     * @param effectType The type of the effect that the player wants to be executed.
     * @param target The targets on which the player wants to execute the effects.
     * @param powerUpCardList The list of powerUpCard that the user may want to use in order to pay
     * the cost of the effect (where needed).
     * @throws EffectException If the said effect cannot be used right now.
     * @throws PropertiesException If some properties of the target are not respected.
     * @throws CardException If the card cannot be used right now.
     */
    public void useCard(EffectType effectType, EffectArgument target,
            List<PowerUpCard> powerUpCardList)
            throws EffectException, PropertiesException, CardException {

        if (!this.isLoaded()) {

            throw new CardNotLoadedException("La carta non è carica.");
        }

        // Launch exception if selected effect not present in map
        if (!this.map.containsKey(effectType)) {

            throw new EffectException("L'effetto selezionato non esiste.");
        }

        // Launch exception if card already used and effectType not optional
        if ((effectType.equals(EffectType.ALTERNATIVE) && this.map.get(EffectType.PRIMARY).isUsed())
                || (effectType.equals(EffectType.PRIMARY) && map.containsKey(EffectType.ALTERNATIVE)
                && this.map.get(EffectType.ALTERNATIVE).isUsed())) {

            throw new EffectUsedException("Non puoi usare l'effetto selezionato adesso.");
        }

        List<Color> costCopy = new ArrayList<>(this.map.get(effectType).getCost());

        Cost.checkCost(this.owner, costCopy, powerUpCardList);

        // Execute selected effect
        this.effectHandler.useEffect(this.map.get(effectType), target);
        this.effectHandler.updateCardUsageVariables(this.map.get(effectType), this);

        costCopy.forEach(x ->
                this.owner.getAmmoCubesList().stream()
                        .filter(y -> y.getColor().equals(x) && !y.isUsed())
                        .findFirst().get()
                        .setUsed(true)
        );
    }

    /**
     * This method creates a JsonObject containing all the information needed in the View. The said
     * JsonObject will add up to every other JsonObject of every other (necessary) class and will be
     * sent to the view when needed.
     *
     * @return The JsonObect containig all the information of this card.
     */
    @Override
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("name", this.name)
                .add("id", this.id)
                .add("isLoaded", this.loaded)
                .add("reloadCost", this.reloadCost.stream()
                        .map(Color::toString)
                        .collect(Collectors.joining(" ")))
                .add("notes", (this.notes != null) ? this.notes : " ")
                .add("primary", this.map.get(EffectType.PRIMARY).toJsonObject())
                .add("alternative", this.map.containsKey(EffectType.ALTERNATIVE)
                        ? this.map.get(EffectType.ALTERNATIVE).toJsonObject()
                        : JsonValue.NULL)
                .add("optional1", this.map.containsKey(EffectType.OPTIONAL_1)
                        ? this.map.get(EffectType.OPTIONAL_1).toJsonObject()
                        : JsonValue.NULL)
                .add("optional2", this.map.containsKey(EffectType.OPTIONAL_2)
                        ? this.map.get(EffectType.OPTIONAL_2).toJsonObject()
                        : JsonValue.NULL)
                .build();
    }

    public static class WeaponCardBuilder {

        /**
         * The EffectHandler of the game.
         */
        private EffectHandler effectHandler;

        /**
         * The id of the card.
         */
        private int id;

        /**
         * The name of the weapon.
         */
        private String name;

        /**
         * The reload cost.
         */
        private List<Color> reloadCost = new ArrayList<>();

        /**
         * The loaded property.
         */
        private boolean loaded = true;

        /**
         * Th Map of EffectType - Effect.
         */
        private EnumMap<EffectType, Effect> map = new EnumMap<>(EffectType.class);

        /**
         * The notes of the Weapon.
         */
        private String notes;

        /**
         * Creates the builder and initializes the effectHandler.
         * @param effectHandler
         */
        public WeaponCardBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        /**
         * Builds the weaponCard.
         * @param jCardObject The JsonObject with the information of the card.
         * @param effectList The List of Effects of this Card.
         * @return The built WeaponCard.
         */
        public WeaponCard build(JsonObject jCardObject, List<Effect> effectList) {

            this.id = jCardObject.getInt("id");
            this.name = jCardObject.getString("name");

            if (jCardObject.containsKey("reloadCost")) {
                jCardObject.getJsonArray("reloadCost").forEach(x ->
                        this.reloadCost.add(Color.valueOf(
                                x.toString().substring(1, x.toString().length() - 1)))
                );
            }

            map.put(EffectType.PRIMARY,
                    effectList.get(jCardObject.getInt("primary") - 1));

            if (jCardObject.containsKey("alternative")) {
                map.put(EffectType.ALTERNATIVE,
                        effectList.get(jCardObject.getInt("alternative") - 1));
            }

            if (jCardObject.containsKey("optional1")) {
                map.put(EffectType.OPTIONAL_1,
                        effectList.get(jCardObject.getInt("optional1") - 1));
            }

            if (jCardObject.containsKey("optional2")) {
                map.put(EffectType.OPTIONAL_2,
                        effectList.get(jCardObject.getInt("optional2") - 1));
            }

            if (jCardObject.containsKey("notes")) {
                this.notes = jCardObject.getString("notes");
            }

            return new WeaponCard(this);
        }
    }
}
