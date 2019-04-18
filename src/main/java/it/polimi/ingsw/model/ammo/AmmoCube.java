package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.Color;

public class AmmoCube implements Ammo {

    private Color color;

    private boolean used;

    public AmmoCube(Color color, boolean used) {

        this.color = color;

        this.used = used;
    }

    @Override
    public Color getColor() {

        return this.color;
    }

    public boolean isUsed() {

        return this.used;
    }

    public void setUsed(boolean used) {

        this.used = used;
    }
}
