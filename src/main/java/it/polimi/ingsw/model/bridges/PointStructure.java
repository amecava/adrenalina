package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.players.Player;

public class PointStructure {

    private Player player;

    private int numberDamage;
    private int firstDamage;
    private int lastDamage;

    public PointStructure(Player player, int numberDamage, int firstDamage, int lastDamage) {

        this.player = player;

        this.numberDamage = numberDamage;
        this.firstDamage = firstDamage;
        this.lastDamage = lastDamage;
    }

    public Player getPlayer() {

        return this.player;
    }

    public int getNumberDamage() {

        return this.numberDamage;
    }

    public void setNumberDamage(int numberDamage) {

        this.numberDamage = numberDamage;
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
}
