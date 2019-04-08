package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.players.Player;

public class PointStructure {
    private Player player;
    private int numberDamage;
    private int firstDamage;
    private int lastDamage;

    public void setLastDamage(int lastDamage) {
        this.lastDamage = lastDamage;
    }

    public int getLastDamage() {
        return lastDamage;
    }

    public PointStructure(Player player, int numberDamage, int firstDamage, int lastDamage) {
        this.player = player;
        this.numberDamage = numberDamage;
        this.firstDamage = firstDamage;
        this.lastDamage=lastDamage;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setNumberDamage(int numberDamage) {
        this.numberDamage = numberDamage;
    }

    public void setFirstDamage(int firstDamage) {
        this.firstDamage = firstDamage;
    }

    public Player getPlayer() {
        return player;
    }

    public int getNumberDamage() {
        return numberDamage;
    }

    public int getFirstDamage() {
        return firstDamage;
    }

    @Override
    public String toString() {
        return (new String("player who made damage "+
                this.getPlayer().getPlayerColor() +
                " number of shots  "+this.numberDamage+
                " the first shot was number " +
                this.firstDamage +
                " last shot was number  " +
                this.getLastDamage()));
    }
}
