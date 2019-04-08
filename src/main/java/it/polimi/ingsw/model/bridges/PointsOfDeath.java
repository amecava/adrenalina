package it.polimi.ingsw.model.bridges;

public class PointsOfDeath {
    int value;
    boolean used;
    //each single method that composes the death bridge of the player
    public void setValue(int value) {
        this.value = value;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getValue() {
        return value;
    }

    public boolean isUsed() {
        return used;
    }

    public PointsOfDeath(int value){
        this.value=value;
        this.used=false;
    }


}
