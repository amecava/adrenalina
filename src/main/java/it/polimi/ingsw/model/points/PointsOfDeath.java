package it.polimi.ingsw.model.points;

public class PointsOfDeath {

    private int value;
    private boolean used = false;

    public PointsOfDeath(int value) {

        this.value = value;
    }

    public boolean isUsed() {

        return this.used;
    }

    public void reset() {

        this.used = false;
    }

    public int getValueSetUsed() {

        this.used = true;

        return this.value;
    }
}
