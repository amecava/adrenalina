package it.polimi.ingsw.model.bridges;

import java.util.ArrayList;
import java.util.List;

public class DeathBridge {

    private int indexOfDeath;
    private List<PointsOfDeath> pointsOfDeaths = new ArrayList<>();

    public DeathBridge() {

        indexOfDeath = 0;
        pointsOfDeaths.add(new PointsOfDeath(8));
        pointsOfDeaths.add(new PointsOfDeath(6));
        pointsOfDeaths.add(new PointsOfDeath(4));
        pointsOfDeaths.add(new PointsOfDeath(2));
        pointsOfDeaths.add(new PointsOfDeath(1));
        pointsOfDeaths.add(new PointsOfDeath(1));
    }

    public void setIndexOfDeath() {

        if (this.indexOfDeath <= 6) {
            this.indexOfDeath++;
        }
    }

    public int getIndexOfDeath() {

        return indexOfDeath;
    }

    public int getPoints() {

        int returnValue = 1;

        for (int i = indexOfDeath; i < pointsOfDeaths.size(); i++) {
            if (!(pointsOfDeaths.get(i).isUsed())) {
                pointsOfDeaths.get(i).setUsed(true);
                returnValue = (pointsOfDeaths.get(i).getValue());

                break;
            }
        }

        return returnValue;
    }

    public void restorePoints() {

        for (int i = indexOfDeath; i < pointsOfDeaths.size(); i++) {
            pointsOfDeaths.get(i).setUsed(false);
        }
    }
}
