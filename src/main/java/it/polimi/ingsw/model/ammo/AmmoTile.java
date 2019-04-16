package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.cards.Card;
import java.util.ArrayList;
import java.util.List;

public class AmmoTile implements Card {

    List<Color> ammoCubesList;
    boolean powerUpCard;

    private AmmoTile(AmmoTileBuilder builder) {

        this.ammoCubesList = builder.ammoCubesList;
        this.powerUpCard = builder.powerUpCard;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Color getColor() {
        throw new UnsupportedOperationException();
    }

    public static class AmmoTileBuilder {

        List<Color> ammoCubesList = new ArrayList<>();
        boolean powerUpCard = false;


    }
}


