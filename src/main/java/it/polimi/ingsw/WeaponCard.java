package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class WeaponCard implements Card {

    private String name;
    private Color color;

    private Effect primary;
    private Effect alternative;

    private List<Effect> optional;

    public WeaponCard(WeaponCardBuilder builder) {
        this.name = builder.name;
        this.color = builder.color;
        this.primary = builder.primary;
        this.alternative = builder.alternative;
        this.optional = builder.optional;
    }

    //Builder Class
    public static class WeaponCardBuilder {
        private String name;
        private Color color;

        private Effect primary;
        private Effect alternative;

        private List<Effect> optional = new ArrayList<>();

        public WeaponCardBuilder(String name, Color color, Effect primary) {
            this.name = name;
            this.color = color;
            this.primary = primary;
        }


        public WeaponCardBuilder setAlternative(Effect alternative) {
            // If optional != null or throw exception
            this.alternative = alternative;
            return this;
        }

        public WeaponCardBuilder setOptional(Effect optional) {
            // If alternative != null or throw exception
            this.optional.add(optional);
            return this;
        }

        public WeaponCard build(){
            return new WeaponCard(this);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Color getColor() {
        return this.color;
    }
}
