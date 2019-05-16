package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.players.Color;
import it.polimi.ingsw.model.ammo.Ammo;
import it.polimi.ingsw.model.ammo.AmmoCube;
import it.polimi.ingsw.model.cards.effects.Effect;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.exceptions.cards.CardNotLoadedException;
import it.polimi.ingsw.model.exceptions.effects.EffectCallException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.Player;
import javax.json.Json;
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

        return this.name;
    }

    @Override
    public Color getColor() {

        return this.color;
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

        target.setWeaponCard(false);

        if (!this.effect.isSameAsPlayer() && this.owner
                .equals(this.effectHandler.getActivePlayer())
                && this.owner.getRemainingActions() != -1) {

            this.effectHandler.useEffect(this.effect.getNext(), target);

        } else if (effect.isSameAsPlayer() &&
                (this.effectHandler.getActive().contains(this.owner)
                        || effectHandler.getInactive().contains(this.owner))) {

            Player tmPlayer = effectHandler.getActivePlayer();
            target.appendTarget(tmPlayer);
            this.effectHandler.setActivePlayer(this.owner);

            this.effectHandler.useEffect(this.effect.getNext(), target);

            this.effectHandler.setActivePlayer(tmPlayer);
            this.effectHandler.getActive().remove(tmPlayer);

        } else {

            throw new CardNotLoadedException("You can't use a power up right now!");
        }
    }

    public void useCard(EffectArgument target, AmmoCube ammoCube)
            throws EffectException, PropertiesException, CardNotLoadedException {

        // Launch exception if wrong method call
        if (effect.getArgs() != target.getArgs()) {

            throw new EffectCallException("Wrong number of arguments to method call!");
        }

        if (color == null || this.effect.getCost().isEmpty()) {

            throw new EffectCallException("Wrong method call!");
        }

        if (effect.isSameAsPlayer() || !this.owner
                .equals(effectHandler.getActivePlayer())
                || this.owner.getRemainingActions() == -1) {

            throw new CardNotLoadedException("You can't use a power up right now!");
        }

        target.setWeaponCard(false);

        this.effectHandler.useEffect(this.effect.getNext(), target);

        ammoCube.setUsed(true);

    }

    @Override
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("name",this.name)
                .add("color", this.color.toString())
                .build();
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

            return new PowerUpCard(this);
        }
    }
}
