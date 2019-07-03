package it.polimi.ingsw.server.model.decks;

import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.Effect;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;

public class WeaponDeck implements Serializable {

    /**
     * The list of WeaponCards.
     */
    private List<WeaponCard> deck;

    /**
     * Builds the deck based on the builder and shuffles it.
     *
     * @param builder The builder.
     */
    private WeaponDeck(WeaponDeckBuilder builder) {

        this.deck = builder.deck;
        Collections.shuffle(this.deck);
    }

    /**
     * Gets one card from the deck.
     *
     * @return A weaponCard.
     */
    public WeaponCard getCard() {

        return deck.remove(0);
    }

    /**
     * Gets a specific WeaponCard from the deck.
     *
     * @param id The id of the card that needs to be drawn from the deck.
     * @return The WeaponCard.
     */
    public WeaponCard getCard(int id) {

        return this.deck.stream()
                .filter(x -> x.getId() == id)
                .findFirst().orElseThrow(NullPointerException::new);
    }

    /**
     * Gets the list of cards.
     *
     * @return The list of WeaponCard.
     */
    public List<WeaponCard> getDeck() {

        return this.deck;
    }

    /**
     * Checks if the deck is empty.
     *
     * @return A boolean that says if the deck is empty.
     */
    public boolean isEmpty() {

        return this.deck.isEmpty();
    }

    public static class WeaponDeckBuilder {

        /**
         * The list of WeaponCards.
         */
        private List<WeaponCard> deck = new ArrayList<>();

        /**
         * The EffectHandler of the game.
         */
        private EffectHandler effectHandler;

        /**
         * The list of effects that will be added to every card (and updated for every card).
         */
        private List<Effect> effectsList = new ArrayList<>();

        /**
         * The JsonArray containing the information of the effects of every card.
         */
        private static JsonArray effects;

        /**
         * The JsonArray with the information of every weapon that will be added to the deck.
         */
        private static JsonArray weapons;

        /**
         * Statically opens the JsonArrays with the information needed to build the cards of the deck.
         */
        static {

            InputStream in;

            in = WeaponDeckBuilder.class.getClassLoader().getResourceAsStream("Effects.json");

            effects = Json.createReader(in).readArray();

            in = WeaponDeckBuilder.class.getClassLoader().getResourceAsStream("WeaponCards.json");

            weapons = Json.createReader(in).readArray();
        }

        /**
         * Creates the builder and initializes the effectHandler.
         *
         * @param effectHandler The effectHandler of the game.
         */
        public WeaponDeckBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        /**
         * Builds the WeaponDeck.
         *
         * @return The built WeaponDeck.
         */
        public WeaponDeck build() {

            this.readEffectsFromJson();
            this.readCardsFromJson();

            return new WeaponDeck(this);

        }

        /**
         * Reads from Json file every effect of every card.
         */
        private void readEffectsFromJson() {

            effects.forEach(x -> this.effectsList
                    .add(new Effect.EffectBuilder(x.asJsonObject()).build())
            );
        }

        /**
         * Reads from Json file the information of every card and adds it to the list.
         */
        private void readCardsFromJson() {

            weapons.forEach(x -> this.deck
                    .add(new WeaponCard.WeaponCardBuilder(this.effectHandler)
                            .build(x.asJsonObject(), this.effectsList))
            );
        }


    }

}
