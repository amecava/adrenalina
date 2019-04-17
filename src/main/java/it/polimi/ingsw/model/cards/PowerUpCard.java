package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Color;
import java.util.List;

public class PowerUpCard implements Card {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public List<Color> getAmmoCubesList() {

        throw new UnsupportedOperationException("This is a PowerUp card, not an AmmoTile card!");
    }

    @Override
    public boolean isPowerUpCard() {

        throw new UnsupportedOperationException("This is a PowerUp card, not an AmmoTile card!");
    }
}
