package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.cards.effects.EffectTarget;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.CardNotLoadedException;
import it.polimi.ingsw.model.exceptions.cards.OwnerNotActiveException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.effects.EffectUsedException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;

public class WeaponCard implements Card {

    private Player owner;
    private EffectHandler effectHandler;

    private int id;

    private String name;
    private boolean loaded;
    private List<Color> reloadCost;

    private EnumMap<EffectType, Effect> map;

    private String notes;

    private WeaponCard(WeaponCardBuilder builder) {

        this.effectHandler = builder.effectHandler;

        this.id = builder.id;

        this.name = builder.name;
        this.loaded = builder.loaded;
        this.reloadCost = builder.reloadCost;

        this.map = builder.map;

        this.notes = builder.notes;
    }

    @Override
    public CardType getCardType() {

        return CardType.WEAPON;
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public Color getColor() {

        return this.reloadCost.get(0);
    }

    public Player getOwner() {

        return this.owner;
    }

    public void setOwner(Player owner) {

        this.owner = owner;
    }

    public int getId() {

        return this.id;
    }

    public boolean isLoaded() {

        return this.loaded;
    }

    public void setLoaded(boolean loaded) {

        this.loaded = loaded;
    }

    public List<Color> getReloadCost() {

        return this.reloadCost;
    }

    public Map<EffectType, Effect> getMap() {

        return this.map;
    }

    public String getNotes() {

        return this.notes;
    }

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

    public void reloadWeapon() {

        // If the card is not loaded
        if (!this.loaded) {

            // TODO Check reload cost

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

            // TODO Remove reload cost from player
        }
    }

    public void activateCard() throws CardException {

        // Launch exception if owner of card is not the active player
        if (!this.effectHandler.getActivePlayer().equals(this.owner)) {

            throw new OwnerNotActiveException("Can't use this card right now!");
        }

        // Launch exception if the card is not loaded
        if (!this.loaded) {

            throw new CardNotLoadedException("Weapon not loaded!");
        }
    }

    public void useCard(EffectType effectType, EffectTarget target)
            throws EffectException, PropertiesException {

        // Launch exception if selected effect not present in map
        if (!this.map.containsKey(effectType)) {

            throw new EffectException("Effect not present!");
        }

        // Launch exception if card already used and effectType not optional
        if ((effectType.equals(EffectType.ALTERNATIVE) && this.map.get(EffectType.PRIMARY).isUsed())
                || (effectType.equals(EffectType.PRIMARY) && map.containsKey(EffectType.ALTERNATIVE)
                && this.map.get(EffectType.ALTERNATIVE).isUsed())) {

            throw new EffectUsedException("Effect can't be used!");
        }

        // TODO check effect cost

        // Execute selected effect
        this.effectHandler.useEffect(this.map.get(effectType), target);
        this.effectHandler.updateCardUsageVariables(this.map.get(effectType), this);

        // TODO remove cost from player
    }

    public static class WeaponCardBuilder {

        private EffectHandler effectHandler;

        private int id;

        private String name;
        private List<Color> reloadCost = new ArrayList<>();
        private boolean loaded = true;

        private EnumMap<EffectType, Effect> map = new EnumMap<>(EffectType.class);

        private String notes;

        public WeaponCardBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

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
