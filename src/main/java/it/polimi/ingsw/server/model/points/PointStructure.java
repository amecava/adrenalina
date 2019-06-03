package it.polimi.ingsw.server.model.points;

import it.polimi.ingsw.server.model.players.Color;
import it.polimi.ingsw.server.model.players.Player;
import java.io.Serializable;
import java.util.List;

public class PointStructure implements Serializable {

    private Player player;

    private int numberDamage;
    private int firstDamage;
    private int lastDamage;

    public PointStructure(Player player) {

        this.player = player;
    }

    Player getPlayer() {

        return this.player;
    }

    int getNumberDamage() {

        return this.numberDamage;
    }

    PointStructure setNumberDamage(int numberDamage) {

        this.numberDamage = numberDamage;

        return this;
    }

    int getFirstDamage() {

        return this.firstDamage;
    }

    int getLastDamage() {

        return this.lastDamage;
    }

    void setLastDamage(int lastDamage) {

        this.lastDamage = lastDamage;
    }

    public PointStructure createPointStructure(List<Color> shots) {

        this.numberDamage = 0;
        boolean foundFirstShot = false;

        for (int i = 0; i < shots.size(); i++) {

            if (shots.get(i).equals(this.player.getColor())) {
                this.numberDamage++;
                this.lastDamage = i + 1;

                if (!foundFirstShot) {
                    this.firstDamage = i + 1;
                    foundFirstShot = true;
                }
            }
        }

        return this;
    }
}
