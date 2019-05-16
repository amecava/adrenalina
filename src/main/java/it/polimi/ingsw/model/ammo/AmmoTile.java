package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardType;
import it.polimi.ingsw.model.cards.PowerUpCard;
import it.polimi.ingsw.model.decks.PowerUpDeck;
import it.polimi.ingsw.model.players.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class AmmoTile implements Card {

    private PowerUpDeck powerUpDeck;
    private List<Color> ammoCubesList;

    private AmmoTile(AmmoTileBuilder builder) {

        this.powerUpDeck = builder.powerUpDeck;
        this.ammoCubesList = builder.ammoCubes;
    }

    @Override
    public CardType getCardType() {

        return CardType.AMMO_TILE;
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
    public String toString() {

        return (this.ammoCubesList.size() == 2) ?
                this.ammoCubesList.stream()
                .map(Color::toString)
                .collect(Collectors.joining(" ")) + " POWER_UP" :
                this.ammoCubesList.stream()
                .map(Color::toString)
                .collect(Collectors.joining(" "));
    }

    @Override
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("colors", this.toString())
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


