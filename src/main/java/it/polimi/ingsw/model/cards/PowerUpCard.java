package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.cards.effects.atomic.AtomicTarget;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import java.util.EnumMap;
import javax.json.JsonObject;

public class PowerUpCard implements Card {

    private EffectHandler effectHandler;

    private String name;
    private Color color;

    private EnumMap<EffectType, Effect> map;

    @Override
    public CardType getCardType() {

        return CardType.POWER_UP;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Color getColor() {
        return null;
    }

    private PowerUpCard(PowerUpCardBuilder builder){

        this.effectHandler = builder.effectHandler;

        this.name = builder.name;
        this.color = builder.color;

        this.map = builder.map;

    }

    public void useCard(EffectType effectType, AtomicTarget target)
            throws EffectException, PropertiesException {

        // Launch exception if selected effect not present in map
        if (!this.map.containsKey(effectType)) {

            throw new EffectException("Effect not present!");
        }

        // Launch exception if card already used and effectType not optional
        /*
        if ((effectType.equals(EffectType.ALTERNATIVE) && this.map.get(EffectType.PRIMARY).isUsed())
                || (effectType.equals(EffectType.PRIMARY) && map.containsKey(EffectType.ALTERNATIVE)
                && this.map.get(EffectType.ALTERNATIVE).isUsed())) {

            throw new EffectUsedException("Effect can't be used!");
        }
        */

        // TODO check effect cost

        // Execute selected effect
        this.effectHandler.useEffect(this.map.get(effectType), target);
        /*
        this.effectHandler.updateCardUsageVariables(this.map.get(effectType), this);
        */
        // TODO remove cost from player
    }

    public static class PowerUpCardBuilder {

        private EffectHandler effectHandler;

        private String name;
        private Color color;

        private EnumMap<EffectType, Effect> map = new EnumMap<>(EffectType.class);

        public PowerUpCardBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        public PowerUpCard build(JsonObject jsonObject){

            this.name = jsonObject.getString("name");
            this.color = Color.valueOf(jsonObject.getString("color"));

            //TODO import the effect and finish Json

            return new PowerUpCard(this);
        }
    }
}
