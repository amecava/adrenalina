package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.Effect;
import java.util.ArrayList;
import java.util.List;

public class WeaponCard implements Card {

    private String name;
    private Color color;

    private int cost;
    private boolean loaded;

    private Effect primary;
    private Effect alternative;

    private List<Effect> optional;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    public int getCost() { return cost; }

    public boolean isLoaded() { return loaded; }

    public Effect getPrimary() {
        return primary;
    }

    public Effect getAlternative() {
        return alternative;
    }

    public List<Effect> getOptional() {
        return optional;
    }

    public Effect getOptional(int index) {
        return optional.get(index);
    }

    public WeaponCard(WeaponCardBuilder builder) {
        this.name = builder.name;
        this.color = builder.color;
        this.cost = builder.cost;
        this.loaded = builder.loaded;
        this.primary = builder.primary;
        this.alternative = builder.alternative;
        this.optional = builder.optional;
    }

    public void reloadWeapon() {
        // Check cost

        if (this.loaded) {
            //
        } else
            this.loaded = true;
    }

    //Builder Class
    public static class WeaponCardBuilder {

        private String name;
        private Color color;

        private int cost;
        private boolean loaded = true;

        private Effect primary;
        private Effect alternative;

        private List<Effect> optional = new ArrayList<>();

        public WeaponCardBuilder(String name, Color color, int cost, Effect primary) {
            this.name = name;
            this.color = color;
            this.cost = cost;
            this.primary = primary;
        }


        public WeaponCardBuilder setAlternative(Effect alternative) throws CardException {
            if (!this.optional.isEmpty())
                throw new CardException("Can't set alternative if optional present!");

            this.alternative = alternative;
            return this;
        }

        public WeaponCardBuilder appendOptional(Effect optional) throws CardException {
            if (this.alternative != null)
                throw new CardException("Can't set optional if alternative present!");

            if (this.optional.size() >= 2)
                throw new CardException("Optional slots full!");

            this.optional.add(optional);
            return this;
        }

        public WeaponCard build() {
            return new WeaponCard(this);
        }
    }
}
