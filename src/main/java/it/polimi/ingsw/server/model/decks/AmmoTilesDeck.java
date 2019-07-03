package it.polimi.ingsw.server.model.decks;

import it.polimi.ingsw.server.model.ammo.AmmoTile;
import it.polimi.ingsw.server.model.cards.Card;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;

public class AmmoTilesDeck implements Serializable {

    /**
     * The list of AmmoTile that are currently in the deck.
     */
    private List<AmmoTile> ammoTilesList;

    /**
     * Builds the deck based on the builder and shuffles the cards.
     *
     * @param builder The builder.
     */
    private AmmoTilesDeck(AmmoTilesDeckBuilder builder) {

        this.ammoTilesList = builder.ammoTilesList;
        Collections.shuffle(this.ammoTilesList);
    }

    public boolean isEmpty() {

        return this.ammoTilesList.isEmpty();
    }

    public Card getTile() {

        return this.ammoTilesList.remove(0);
    }

    /**
     * Adds an AmmoTile to the deck.
     */
    public void addTile(AmmoTile tile) {

        this.ammoTilesList.add(tile);
    }

    public static class AmmoTilesDeckBuilder {

        /**
         * The deck of powerUps.
         */
        private PowerUpDeck powerUpDeck;

        /**
         * The list of every AmmoTile.
         */
        private List<AmmoTile> ammoTilesList = new ArrayList<>();

        /**
         * The JsonObject with the information to build the deck.
         */
        private static JsonArray object;

        /**
         * Statically opens the "AmmoTiles.json" resource file.
         */
        static {

            InputStream in = AmmoTilesDeckBuilder.class.getClassLoader()
                    .getResourceAsStream("AmmoTiles.json");

            object = Json.createReader(in).readArray();
        }

        /**
         * Builds the deck and assigns the reference to the PowerUpDeck to the AmmoTiles that need
         * it.
         *
         * @param powerUpDeck The deck with all the power ups.
         */
        public AmmoTilesDeckBuilder(PowerUpDeck powerUpDeck) {

            this.powerUpDeck = powerUpDeck;

            this.readTilesFromJson();
        }

        /**
         * Read from Json file the information needed to build the deck.
         */
        private void readTilesFromJson() {

            object.forEach(x -> this.ammoTilesList
                    .add(new AmmoTile.AmmoTileBuilder(x.asJsonObject(), this.powerUpDeck).build())
            );
        }

        /**
         * Builds the Deck.
         *
         * @return The deck.
         */
        public AmmoTilesDeck build() {

            return new AmmoTilesDeck(this);
        }

    }
}
