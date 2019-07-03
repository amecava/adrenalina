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

    /**
     * The EffectHandler of the game.
     */
    private EffectHandler effectHandler;

    /**
     * The name of the Power Up.
     */
    private String name;

    /**
     * The color of the Power Up.
     */
    private Color color;

    /**
     * The description of the Power Up.
     */
    private String info;

    /**
     * The current owner of the Power Up.
     */
    private Player owner;

    /**
     * The Effect object with the information on how to execute the effect of the Power Up.
     */
    private Effect effect;

    /**
     * Gets the name of the Power Up.
     *
     * @return The name of the Power Up.
     */
    @Override
    public String getName() {

        return this.name;
    }

    /**
     * The Color of the Power up.
     *
     * @return The Color of the Power Up.
     */
    @Override
    public Color getColor() {

        return this.color;
    }

    /**
     * Build the power up based on the builder.
     *
     * @param builder The builder.
     */
    private PowerUpCard(PowerUpCardBuilder builder) {

        this.effectHandler = builder.effectHandler;

        this.name = builder.name;
        this.color = builder.color;
        this.info = builder.info;

        this.effect = builder.effect;

    }

    /**
     * Sets the current owner of the Card.
     *
     * @param owner The Player who owns the card.
     */
    public void setOwner(Player owner) {

        this.owner = owner;
    }

    /**
     * This method performs the "usePowerUp" action by applying its effect.
     *
     * @param target The target of the effect.
     * @throws EffectException If the said effect cannot be used right now.
     * @throws PropertiesException If some properties of the target are not respected.
     * @throws CardException If the card cannot be used right now.
     */
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

    /**
     * This method performs the "usePowerUp" action by applying its effect. This is the case of the
     * power up that needs an ammo cube as a payments
     *
     * @param target The target of the effect.
     * @param color The color of the ammo cube that the user wanted to use in order to pay the
     * effect.
     * @throws EffectException If the said effect cannot be used right now.
     * @throws PropertiesException If some properties of the target are not respected.
     * @throws CardException If the card cannot be used right now.
     */
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

    /**
     * This method creates a JsonObject containing all the information needed in the View. The said
     * JsonObject will add up to every other JsonObject of every other (necessary) class and will be
     * sent to the view when needed.
     *
     * @return The JsonObject containing all the information of this card.
     */
    @Override
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("name", this.name)
                .add("color", this.color.toString())
                .add("targetType", this.effect.getNext().getTargetType().toString())
                .add("args", this.effect.getArgs())
                .add("hasCost", !this.effect.getCost().isEmpty())
                .add("info", this.info)
                .build();
    }

    public static class PowerUpCardBuilder {

        /**
         * The EffectHandler of the game.
         */
        private EffectHandler effectHandler;

        /**
         * The name of the power up.
         */
        private String name;

        /**
         * The color of the power up.
         */
        private Color color;

        /**
         * The description of the power up.
         */
        private String info;

        /**
         * The effect of the power up.
         */
        private Effect effect;

        /**
         * Creates the builder by initializing the effectHandler.
         *
         * @param effectHandler The effectHandler of the game.
         */
        public PowerUpCardBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        /**
         * Builds the PowerUpCard by reading the information of the card.
         *
         * @param jsonObject The JsonObject with the information of the Power Up.
         * @return The built PowerUpCard.
         */
        public PowerUpCard build(JsonObject jsonObject) {

            this.name = jsonObject.getString("name");
            this.color = Color.valueOf(jsonObject.getString("color"));
            this.info = jsonObject.getString("info");

            this.effect = new Effect.EffectBuilder(jsonObject.getJsonObject("effect")).build();

            return new PowerUpCard(this);
        }
    }
}
