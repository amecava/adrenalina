package it.polimi.ingsw.model.decks;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

public class AmmoTilesDeck {

    List<AmmoTile> ammoTilesList;

    private AmmoTilesDeck(AmmoTilesDeckBuilder builder){

        this.ammoTilesList = builder.ammoTilesList;
        //Collections.shuffle(this.ammoTilesList);
    }

    public Card getTile() throws IllegalActionException {
        if (this.ammoTilesList.isEmpty())
            throw new IllegalActionException("Ammo Tile Deck is empty");
        return this.ammoTilesList.remove(0);
    }
    // useful for tests

    public AmmoTile getTile(int i){
        return this.ammoTilesList.remove(i);
    }

    public void pushAmmoTile(AmmoTile ammoTile){
        this.ammoTilesList.add(ammoTile);
    }

    public static class AmmoTilesDeckBuilder{

        List<AmmoTile> ammoTilesList = new ArrayList<>();

        public AmmoTilesDeckBuilder(){

            this.readTilesFromJson();
        }

        private void readTilesFromJson() {

            try (JsonReader tReader = Json.createReader(new FileReader("lib/cards/AmmoTiles.json"))) {

                JsonArray jTilesArray = tReader.readArray();

                jTilesArray.forEach(x ->
                        this.ammoTilesList.add(new AmmoTile.AmmoTileBuilder(x.asJsonObject()).build())
                );

            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }

        public AmmoTilesDeck build(){

            return new AmmoTilesDeck(this);
        }

    }
}
