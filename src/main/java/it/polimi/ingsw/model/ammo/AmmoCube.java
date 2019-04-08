package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.Color;

public class AmmoCube implements Ammo {

    private Color color;

    public AmmoCube(Color color) {

        this.color = color;
    }

    @Override
    public Color getColor() {

        return this.color;
    }
}
