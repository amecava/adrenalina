package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.ammo.Ammo;
import it.polimi.ingsw.model.ammo.AmmoCube;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicTarget;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.CardNotLoadedException;
import it.polimi.ingsw.model.exceptions.cards.OwnerNotActiveException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.effects.EffectUsedException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonObject;

public class WeaponCard implements Card {

    private Player owner;
    private EffectHandler effectHandler;

    private int id;

    private String name;
    private boolean loaded;
    private List<Ammo> reloadCost;

    private Effect primary;
    private Effect alternative;

    private List<Effect> optional;

    private String notes;

    private WeaponCard(WeaponCardBuilder builder) {

        this.effectHandler = builder.effectHandler;

        this.id = builder.id;

        this.name = builder.name;
        this.loaded = builder.loaded;
        this.reloadCost = builder.reloadCost;

        this.primary = builder.primary;
        this.alternative = builder.alternative;

        this.optional = builder.optional;

        this.notes = builder.notes;
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

    public List<Ammo> getReloadCost() {

        return this.reloadCost;
    }

    public boolean isLoaded() {

        return this.loaded;
    }

    public void setLoaded(boolean loaded) {

        this.loaded = loaded;
    }

    public Effect getPrimary() {

        return this.primary;
    }

    public Effect getAlternative() {

        return this.alternative;
    }

    public List<Effect> getOptional() {

        return this.optional;
    }

    public Effect getOptional(int index) {

        return this.optional.get(index);
    }

    public String getNotes() {

        return this.notes;
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public Color getColor() {

        return this.reloadCost.get(0).getColor();
    }

    public void reloadWeapon() {

        // If the card is not loaded
        if (!this.loaded) {
            // TODO Check reload cost
            // TODO Remove reload cost from player

            // Set primary effect used flag to false
            this.primary.setUsed(false);

            // Set alternative effect used flag to false if present
            if (this.alternative != null) {

                this.alternative.setUsed(false);
            }

            // Set optional effects used flag to false
            this.optional.forEach(x ->
                    x.setUsed(false));

            // Deactivate optionals if they've been activated
            this.optional.forEach(x -> {
                if (x.getActivated() != null) {
                    x.setActivated(false);
                }
            });

            // Set card loaded flag to true
            this.loaded = true;
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

    public void usePrimary(AtomicTarget target)
            throws EffectException, PropertiesException {

        // Launch exception if alternative effect used
        if (this.alternative != null && this.alternative.isUsed()) {

            throw new EffectUsedException("Card not loaded, primary effect used!");
        }

        // Execute primary effect
        this.effectHandler.useEffect(this.primary, target);
        this.effectHandler.updateCardUsageVariables(this.primary, this);
    }

    public void useAlternative(AtomicTarget target)
            throws EffectException, PropertiesException {

        // Launch exception if alternative effect not present
        if (this.alternative == null) {

            throw new EffectException("Alternative effect not present!");
        }

        // Launch exception if primary effect used
        if (this.primary.isUsed()) {

            throw new EffectUsedException("Card not loaded, alternative effect used!");
        }

        // TODO Check effect cost

        // Execute alternative effect
        this.effectHandler.useEffect(this.alternative, target);
        this.effectHandler.updateCardUsageVariables(this.alternative, this);

        // TODO Remove effect cost from player
    }

    public void useOptional(int index, AtomicTarget target)
            throws EffectException, PropertiesException {

        // Launch exception if the optional effect at selected index doesn't exists
        try {

            // TODO Check effect cost
        } catch (IndexOutOfBoundsException e) {

            throw new EffectException("Optional effect at selected index not present!");
        }

        // Execute optional effect
        this.effectHandler.useEffect(this.optional.get(index), target);
        this.effectHandler.updateCardUsageVariables(this.optional.get(index), this);

        // TODO Remove effect cost from player
    }

    public static class WeaponCardBuilder {

        private EffectHandler effectHandler;

        private int id;

        private String name;
        private List<Ammo> reloadCost = new ArrayList<>();
        private boolean loaded = true;

        private Effect primary;
        private Effect alternative;

        private List<Effect> optional = new ArrayList<>();

        private String notes;

        public WeaponCardBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;

        }

        public WeaponCard build(JsonObject jCardObject, List<Effect> effectList) {

            this.id = jCardObject.getInt("id");
            this.name = jCardObject.getString("name");

            if (jCardObject.containsKey("reloadCost")) {
                jCardObject.getJsonArray("reloadCost").forEach(x ->
                        this.reloadCost.add(new AmmoCube(Color.valueOf(
                                x.toString().substring(1, x.toString().length() - 1))))
                );
            }

            this.primary = effectList.get(jCardObject.getInt("primary") - 1);

            if (jCardObject.containsKey("alternative")) {
                this.alternative = effectList.get(jCardObject.getInt("alternative") - 1);
            }

            if (jCardObject.containsKey("optional")) {
                jCardObject.getJsonArray("optional").forEach(x ->
                        this.optional.add(effectList.get(Integer.parseInt(x.toString()) - 1))
                );
            }

            if (jCardObject.containsKey("notes")) {
                this.notes = jCardObject.getString("notes");
            }

            return new WeaponCard(this);
        }
    }
}
