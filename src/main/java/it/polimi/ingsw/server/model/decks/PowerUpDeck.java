package it.polimi.ingsw.server.model.decks;

import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;

public class PowerUpDeck implements Serializable {

    /**
     * The list of the PowerUpCards that are currently im the deck.
     */
    private List<PowerUpCard> deck;

    /**
     * Initializes the deck based on the builder and shuffles the deck.
     *
     * @param builder The builder.
     */
    private PowerUpDeck(PowerUpDeckBuilder builder) {

        this.deck = builder.deck;
        Collections.shuffle(this.deck);
    }

    /**
     * Gets the deck.
     *
     * @return The list of Cards.
     */
    public List<PowerUpCard> getDeck() {

        return this.deck;
    }

    /**
     * Gets the first card of the list.
     *
     * @return A PowerUpCard.
     */
    public PowerUpCard getPowerUpCard() {

        return this.deck.remove(0);
    }

    /**
     * Adds a PowerUpCard to the deck.
     *
     * @param powerUpCard A PowerUpCard.
     */
    public void addPowerUpCard(PowerUpCard powerUpCard) {

        this.deck.add(powerUpCard);
    }

    public static class PowerUpDeckBuilder {

        /**
         * The list to be filled with Cards read from Json file.
         */
        List<PowerUpCard> deck = new ArrayList<>();

        /**
         * The EffectHandler of the game.
         */
        EffectHandler effectHandler;

        /**
         * The JsonObject with the information to build the deck.
         */
        private static JsonArray object;

        /**
         * Statically opens the Json file to read information.
         */
        static {

            InputStream in = PowerUpDeckBuilder.class.getClassLoader()
                    .getResourceAsStream("PowerUps.json");

            object = Json.createReader(in).readArray();
        }


        /**
         * Initializes the Builder with the effectHandler.
         */
        public PowerUpDeckBuilder(EffectHandler effectHandler) {

            this.effectHandler = effectHandler;
        }

        /**
         * Builds the Deck.
         *
         * @return The PowerUpDeck.
         */
        public PowerUpDeck build() {

            this.readFromJson();

            return new PowerUpDeck(this);

        }

        /**
         * Reads the Json file with the information of the cards.
         */
        private void readFromJson() {

            object.forEach(x -> this.deck
                    .add(new PowerUpCard.PowerUpCardBuilder(effectHandler).build(x.asJsonObject()))
            );
        }

    }

}
