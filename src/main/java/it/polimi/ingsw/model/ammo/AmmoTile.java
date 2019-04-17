package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.Card;
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonObject;

public class AmmoTile implements Card {

    List<Color> ammoCubesList;
    boolean powerUpCard;

    private AmmoTile(AmmoTileBuilder builder) {

        this.ammoCubesList = builder.ammoCubes;
        this.powerUpCard = builder.powerUpCard;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("This is an AmmoTile, it doesn't have a name");
    }

    @Override
    public Color getColor() {
        throw new UnsupportedOperationException("This is an AmmoTile, it doesn't have a color");
    }

    @Override
    public List<Color> getAmmoCubesList() {

        return this.ammoCubesList;
    }

    @Override
    public boolean isPowerUpCard() {

        return this.powerUpCard;
    }

    public static class AmmoTileBuilder {

        List<Color> ammoCubes = new ArrayList<>();
        boolean powerUpCard;

        public AmmoTileBuilder(JsonObject jTileObject) {

            jTileObject.getJsonArray("cubes").forEach(x -> this.ammoCubes
                    .add(Color.valueOf(x.toString().substring(1, x.toString().length() - 1))));

            this.powerUpCard = jTileObject.getBoolean("powerUp");

        }

        public AmmoTile build() {

            return new AmmoTile(this);
        }

    }
}


