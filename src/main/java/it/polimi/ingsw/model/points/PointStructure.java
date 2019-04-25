package it.polimi.ingsw.model.points;

import it.polimi.ingsw.model.players.Player;
import it.polimi.ingsw.model.players.bridges.Shots;
import java.util.List;

public class PointStructure {

    private Player player;

    private int numberDamage;
    private int firstDamage;
    private int lastDamage;

    public PointStructure(Player player) {

        this.player = player;
    }

    public Player getPlayer() {

        return this.player;
    }

    public int getNumberDamage() {

        return this.numberDamage;
    }

    public PointStructure setNumberDamage(int numberDamage) {

        this.numberDamage = numberDamage;

        return this;
    }

    public int getFirstDamage() {

        return this.firstDamage;
    }

    public void setFirstDamage(int firstDamage) {

        this.firstDamage = firstDamage;
    }

    public int getLastDamage() {

        return this.lastDamage;
    }

    public void setLastDamage(int lastDamage) {

        this.lastDamage = lastDamage;
    }

    public PointStructure createPointStructure(List<Shots> shots) {

        this.numberDamage = 0;
        boolean foundFirstShot = false;

        for (int i = 0; i < shots.size(); i++) {

            if (shots.get(i).getColor().equals(this.player.getColor())) {
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
