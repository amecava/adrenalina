package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.ammo.Ammo;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.exceptions.cards.CardNotLoadedException;
import it.polimi.ingsw.model.exceptions.effects.EffectCallException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import javax.json.JsonObject;

public class PowerUpCard implements Card, Ammo {

    private EffectHandler effectHandler;

    private String name;
    private Color color;
    private Player owner;

    private Effect effect;

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

    private PowerUpCard(PowerUpCardBuilder builder) {

        this.effectHandler = builder.effectHandler;

        this.name = builder.name;
        this.color = builder.color;

        this.effect = builder.effect;

    }

    public void setOwner(Player owner) {

        this.owner = owner;
    }

    public void useCard(EffectArgument target)
            throws EffectException, PropertiesException, CardNotLoadedException {

        // Launch exception if wrong method call
        if (this.effect.getArgs() != target.getArgs()) {

            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        if (!this.effect.getCost().isEmpty()) {

            throw new EffectCallException("Wrong method call!");
        }

        if (!this.effect.getSameAsFather(0) && this.owner
                .equals(this.effectHandler.getActivePlayer())) {

            this.effectHandler.useEffect(this.effect.getNext(), target);

        } else if (effect.getSameAsFather(0) && this.effectHandler.getActive().contains(this.owner) && (
                this.effectHandler.getActive().contains(this.owner)
                        || effectHandler.getInactive().contains(this.owner))) {

            Player tmPlayer = effectHandler.getActivePlayer();

            target.appendTarget(tmPlayer);

            this.effectHandler.setActivePlayer(this.owner);

            this.effectHandler.useEffect(this.effect.getNext(), target);

            this.effectHandler.setActivePlayer(tmPlayer);
            this.effectHandler.getActive().remove(tmPlayer);
        }

        throw new CardNotLoadedException("You can't use a power up right now!");
    }

    public Ammo useCard(EffectArgument target, Ammo ammo)
            throws EffectException, PropertiesException, CardNotLoadedException {

        // Launch exception if wrong method call
        if (effect.getArgs() != target.getArgs()) {

            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        if (ammo == null || this.effect.getCost().isEmpty()) {

            throw new EffectCallException("Wrong method call!");
        }

        if (!effect.getSameAsFather(0) && this.owner
                .equals(effectHandler.getActivePlayer())) {


            this.effectHandler.useEffect(this.effect.getNext(), target);

            return ammo;
        }

        throw new CardNotLoadedException("You can't use a power up right now!");
    }

    public static class PowerUpCardBuilder {

        private EffectHandler effectHandler;

        private String name;
        private Color color;

        private Effect effect;

        public PowerUpCardBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        public PowerUpCard build(JsonObject jsonObject) {

            this.name = jsonObject.getString("name");
            this.color = Color.valueOf(jsonObject.getString("color"));
            this.effect = new Effect.EffectBuilder(jsonObject.getJsonObject("effect")).build();
            //TODO finish Json

            return new PowerUpCard(this);
        }
    }
}
