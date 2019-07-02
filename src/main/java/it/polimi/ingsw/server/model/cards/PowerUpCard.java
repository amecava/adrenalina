package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.cards.CostException;
import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.ammo.Ammo;
import it.polimi.ingsw.server.model.ammo.AmmoCube;
import it.polimi.ingsw.server.model.cards.effects.Effect;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.exceptions.cards.CardNotLoadedException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectCallException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonObject;

public class PowerUpCard implements Card, Ammo, Serializable {

    private EffectHandler effectHandler;

    private String name;
    private Color color;
    private String info;

    private Player owner;

    private Effect effect;

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
        this.info = builder.info;

        this.effect = builder.effect;

    }

    public void setOwner(Player owner) {

        this.owner = owner;
    }

    public void useCard(EffectArgument target)
            throws EffectException, PropertiesException, CardNotLoadedException {

        // Launch exception if wrong method call
        if (this.effect.getArgs() != target.getArgs()) {

            throw new EffectCallException("Errore! Riprova selezionando le cose giuste.");
        }

        if (!this.effect.getCost().isEmpty()) {

            throw new EffectCallException("Attenzione: errore! Riprova");
        }

        target.setWeaponCard(false);

        if (!this.effect.isSameAsPlayer() && this.owner
                .equals(this.effectHandler.getActivePlayer())
                && this.owner.getRemainingActions() != -1) {

            this.effectHandler.useEffect(this.effect.getNext(), target);

        } else if (effect.isSameAsPlayer() &&
                (this.effectHandler.getActive().contains(this.owner)
                        || effectHandler.getInactive().contains(this.owner))) {

                synchronized (EffectHandler.class) {

                    Player player = effectHandler.getActivePlayer();
                    target.appendTarget(player);

                    this.effectHandler.setActivePlayer(this.owner);

                    this.effectHandler.useEffect(this.effect.getNext(), target);

                    this.effectHandler.setActivePlayer(player);
                }

        } else {

            throw new CardNotLoadedException("Non puoi usare questo power up adesso!");
        }
    }

    public void useCard(EffectArgument target, Color color)
            throws EffectException, PropertiesException, CardException {

        // Launch exception if wrong method call
        if (effect.getArgs() != target.getArgs()) {

            throw new EffectCallException("Errore! Riprova selezionando le cose giuste.");
        }

        if (color == null || this.effect.getCost().isEmpty()) {

            throw new EffectCallException("Attenzione: errore! Riprova");
        }

        if (effect.isSameAsPlayer() || !this.owner
                .equals(effectHandler.getActivePlayer())
                || this.owner.getRemainingActions() == -1) {

            throw new CardNotLoadedException("Non puoi usare questo power up adesso!");
        }

        AmmoCube ammoCube = this.owner.getAmmoCubesList().stream()
                .filter(x -> x.getColor().equals(color) && !x.isUsed())
                .findAny()
                .orElseThrow(() -> new CostException("Non hai il cubo munizione selezionato."));

        target.setWeaponCard(false);

        this.effectHandler.useEffect(this.effect.getNext(), target);

        ammoCube.setUsed(true);
    }

    @Override
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("name", this.name)
                .add("color", this.color.toString())
                .add("targetType", this.effect.getNext().getTargetType().toString())
                .add("args", this.effect.getNext().getArgs())
                .add("hasCost", !this.effect.getCost().isEmpty())
                .add("info", this.info)
                .build();
    }

    public static class PowerUpCardBuilder {

        private EffectHandler effectHandler;

        private String name;
        private Color color;
        private String info;

        private Effect effect;

        public PowerUpCardBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        public PowerUpCard build(JsonObject jsonObject) {

            this.name = jsonObject.getString("name");
            this.color = Color.valueOf(jsonObject.getString("color"));
            this.info = jsonObject.getString("info");

            this.effect = new Effect.EffectBuilder(jsonObject.getJsonObject("effect")).build();

            return new PowerUpCard(this);
        }
    }
}
