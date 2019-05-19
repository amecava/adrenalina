package it.polimi.ingsw.model.decks;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cards.Card;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

public class AmmoTilesDeck {

    private List<AmmoTile> ammoTilesList;

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

    public static class AmmoTilesDeckBuilder {

        private PowerUpDeck powerUpDeck;

        private List<AmmoTile> ammoTilesList = new ArrayList<>();

        public AmmoTilesDeckBuilder(PowerUpDeck powerUpDeck) {

            this.powerUpDeck = powerUpDeck;

            this.readTilesFromJson();
        }

        private void readTilesFromJson() {

            try (JsonReader tReader = Json
                    .createReader(new FileReader("lib/cards/AmmoTiles.json"))) {

                JsonArray jTilesArray = tReader.readArray();

                jTilesArray.forEach(x ->
                        this.ammoTilesList
                                .add(new AmmoTile.AmmoTileBuilder(x.asJsonObject(), this.powerUpDeck).build())
                );

            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }

        public AmmoTilesDeck build() {

            return new AmmoTilesDeck(this);
        }

    }
}
