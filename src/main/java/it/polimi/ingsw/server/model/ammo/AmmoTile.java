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

    /**
     * A reference to the PowerUpDeck. Some of the ammo tiles only has two ammo cubes. In that cases
     * the player is allowed to draw a power up from the PowerUpDeck.
     */
    private PowerUpDeck powerUpDeck;

    /**
     * The ammo tile has two or three colors representing the ammo cube the player needs to draw.
     */
    private List<Color> ammoCubesList;

    private AmmoTile(AmmoTileBuilder builder) {

        this.powerUpDeck = builder.powerUpDeck;
        this.ammoCubesList = builder.ammoCubes;
    }

    /**
     * This is an unsupported operation since this class implements Card, an interface that declares
     * this method that, by the way, is meaningless here.
     */
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

    /**
     * This method says if the ammo tile gives the player the opportunity to draw a power up.
     *
     * @return True/false based on the property cited in the attributes documentation.
     */
    public boolean hasPowerUpCard() {

        return this.ammoCubesList.size() != 3 && !this.powerUpDeck.getDeck().isEmpty();
    }

    /**
     * When the ammo tile has the property of giving the power up to the player, this method makes
     * it available to do so from this class.
     */
    public PowerUpCard getPowerUpCard() {

        return this.powerUpDeck.getPowerUpCard();
    }


    /**
     * This method creates a JsonObject containing all the information needed in the View. The said
     * JsonObject will add up to every other JsonObject of every other (necessary) class and will be
     * sent to the view when needed.
     */
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

        /**
         * Analyzes the JsonObject read from the Resources package to dynamically build the ammo
         * tiles.
         */
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


