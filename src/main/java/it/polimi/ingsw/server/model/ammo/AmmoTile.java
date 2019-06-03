package it.polimi.ingsw.server.model.ammo;

import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.decks.PowerUpDeck;
import it.polimi.ingsw.server.model.players.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class AmmoTile implements Card, Serializable {

    private PowerUpDeck powerUpDeck;
    private List<Color> ammoCubesList;

    private AmmoTile(AmmoTileBuilder builder) {

        this.powerUpDeck = builder.powerUpDeck;
        this.ammoCubesList = builder.ammoCubes;
    }

    @Override
    public String getName() {

        throw new UnsupportedOperationException("This is an AmmoTile, it doesn't have a name");
    }

    @Override
    public Color getColor() {

        throw new UnsupportedOperationException("This is an AmmoTile, it doesn't have a color");
    }

    public List<Color> getAmmoCubesList() {

        return this.ammoCubesList;
    }

    public boolean hasPowerUpCard() {

        return this.ammoCubesList.size() != 3 && !this.powerUpDeck.getDeck().isEmpty();
    }

    public PowerUpCard getPowerUpCard() {

        return this.powerUpDeck.getPowerUpCard();
    }


    @Override
    public JsonObject toJsonObject() {

        JsonArrayBuilder builder = Json.createArrayBuilder();

        this.ammoCubesList.stream()
                .map(Color::toString)
                .forEach(builder::add);

        return Json.createObjectBuilder()
                .add("colors", builder.build())
                .build();
    }

    public static class AmmoTileBuilder {

        private PowerUpDeck powerUpDeck;
        private List<Color> ammoCubes = new ArrayList<>();

        public AmmoTileBuilder(JsonObject jTileObject, PowerUpDeck powerUpDeck) {

            this.powerUpDeck = powerUpDeck;

            jTileObject.getJsonArray("cubes").forEach(x -> this.ammoCubes
                    .add(Color.valueOf(x.toString().substring(1, x.toString().length() - 1))));

        }

        public AmmoTile build() {

            return new AmmoTile(this);
        }
    }
}


