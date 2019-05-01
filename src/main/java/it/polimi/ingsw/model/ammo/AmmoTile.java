package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardType;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonObject;

public class AmmoTile implements Card {

    private List<Color> ammoCubesList;

    private AmmoTile(AmmoTileBuilder builder) {

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

        return this.ammoCubesList.size() != 3;
    }

    public static class AmmoTileBuilder {

        List<Color> ammoCubes = new ArrayList<>();

        public AmmoTileBuilder(JsonObject jTileObject) {

            jTileObject.getJsonArray("cubes").forEach(x -> this.ammoCubes
                    .add(Color.valueOf(x.toString().substring(1, x.toString().length() - 1))));

        }

        public AmmoTile build() {

            return new AmmoTile(this);
        }

    }
}


