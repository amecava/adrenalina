package it.polimi.ingsw.server.model.ammo;

import it.polimi.ingsw.server.model.players.Color;
import java.io.Serializable;

public class AmmoCube implements Ammo, Serializable {

    /**
     * The color of the cube.
     */
    private Color color;

    /**
     * A boolean that says if a certain ammo cube is available to use or not. It is true if the
     * player has it, and false if the player alredy used it.
     */
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
