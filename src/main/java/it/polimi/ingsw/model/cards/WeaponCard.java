package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.ammo.Ammo;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import java.util.ArrayList;
import java.util.List;

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

    public void setOwner(Player owner) {

        this.owner = owner;
    }

    public int getId() {

        return this.id;
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public Color getColor() {

        return this.reloadCost.get(0).getColor();
    }

    public List<Ammo> getReloadCost() {

        return this.reloadCost;
    }

    public boolean isLoaded() {

        return this.loaded;
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

    public void reloadWeapon() {

        if (!this.loaded) {
            // check this.reloadCost
            // Remove cost from player

            this.loaded = true;

            this.primary.setUsed(false);

            if (this.alternative != null) {
                this.alternative.setUsed(false);
            }

            this.optional.stream()
                    .forEach(x -> x.setUsed(false));

            this.optional.stream()
                    .forEach(x -> {
                        if (x.getEffectProperties().getActivated() != null) {
                            x.getEffectProperties().setActivated(false);
                        }
                    });
        }
    }

    public void unloadWeapon() {

        this.loaded = false;
    }

    public void useCard() throws CardException {

        effectHandler.setPlayerCard(this.owner, this);
    }

    public void usePrimary(Square square, List<Target> target)
            throws EffectException, PropertiesException {

        effectHandler.useEffect(this.primary, square, target);
    }

    public void useAlternative(Square square, List<Target> target)
            throws CardException, EffectException, PropertiesException {

        if (this.alternative == null) {
            throw new CardException("Alternative effect not present!");
        }

        // check this.alternative.getEffectProperties().getCost() PropertiesException

        effectHandler.useEffect(this.alternative, square, target);
    }

    public void useOptional(int index, Square square, List<Target> target)
            throws CardException, EffectException, PropertiesException {

        try {
            // check this.optional.get(index).getEffectProperties().getCost() PropertiesException
        } catch (IndexOutOfBoundsException e) {
            throw new CardException("Optional effect at selected index not present!");
        }

        effectHandler.useEffect(this.optional.get(index), square, target);
    }

    public static class WeaponCardBuilder {

        private EffectHandler effectHandler;

        private int id;

        private String name;
        private List<Ammo> reloadCost;
        private boolean loaded = true;

        private Effect primary;
        private Effect alternative;

        private List<Effect> optional = new ArrayList<>();

        private String notes;

        public WeaponCardBuilder(EffectHandler effectHandler, int id, String name, List<Ammo> cost,
                Effect primary) {

            this.effectHandler = effectHandler;

            this.id = id;

            this.name = name;
            this.reloadCost = cost;

            this.primary = primary;
        }

        public WeaponCardBuilder setAlternative(Effect alternative) throws CardException {

            if (!this.optional.isEmpty()) {
                throw new CardException("Can't set alternative if optional present!");
            }

            this.alternative = alternative;
            return this;
        }

        public WeaponCardBuilder appendOptional(Effect optional) throws CardException {

            if (this.alternative != null) {
                throw new CardException("Can't set optional if alternative present!");
            }

            if (this.optional.size() >= 2) {
                throw new CardException("Optional slots full!");
            }

            this.optional.add(optional);
            return this;
        }

        public WeaponCardBuilder setNote(String notes) {

            this.notes = notes;
            return this;
        }

        public WeaponCard build() {

            return new WeaponCard(this);
        }
    }
}
