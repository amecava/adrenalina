package it.polimi.ingsw.model.points;

public class PointsOfDeath {

    private int value;
    private boolean used;

    public PointsOfDeath(int value) {

        this.value = value;
        this.used = false;
    }

    public int getValue() {

        return this.value;
    }

    public boolean isUsed() {

        return this.used;
    }

    public void setUsed(boolean used) {

        this.used = used;
    }
}
