package it.polimi.ingsw.model.bridges;

public class PointsOfDeath {

    int value;
    boolean used;

    public PointsOfDeath(int value) {

        this.value = value;
        this.used = false;
    }

    public void setValue(int value) {

        this.value = value;
    }

    public void setUsed(boolean used) {

        this.used = used;
    }

    public int getValue() {

        return this.value;
    }

    public boolean isUsed() {

        return this.used;
    }
}
