package it.polimi.ingsw.server.model.decks;

import it.polimi.ingsw.server.model.ammo.AmmoTile;
import it.polimi.ingsw.server.model.cards.Card;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;

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

        private static JsonArray object;

        static {

            InputStream in = AmmoTilesDeckBuilder.class.getClassLoader().getResourceAsStream("AmmoTiles.json");

            object = Json.createReader(in).readArray();
        }

        public AmmoTilesDeckBuilder(PowerUpDeck powerUpDeck) {

            this.powerUpDeck = powerUpDeck;

            this.readTilesFromJson();
        }

        private void readTilesFromJson() {

            object.forEach(x -> this.ammoTilesList
                    .add(new AmmoTile.AmmoTileBuilder(x.asJsonObject(), this.powerUpDeck).build())
            );
        }

        public AmmoTilesDeck build() {

            return new AmmoTilesDeck(this);
        }

    }
}
