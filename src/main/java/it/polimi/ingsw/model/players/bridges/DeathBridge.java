package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.points.PointsOfDeath;
import java.util.ArrayList;
import java.util.List;

class DeathBridge {

    private int kills;
    private List<PointsOfDeath> pointsOfDeaths = new ArrayList<>();

    DeathBridge() {

        this.kills = 0;
        this.pointsOfDeaths.add(new PointsOfDeath(8));
        this.pointsOfDeaths.add(new PointsOfDeath(6));
        this.pointsOfDeaths.add(new PointsOfDeath(4));
        this.pointsOfDeaths.add(new PointsOfDeath(2));
    }

    void addKill() {
        this.kills++;
    }

    int calculatePoints() {

        int returnValue = 1;

        for (int i = this.kills; i < this.pointsOfDeaths.size(); i++) {
            if (!(this.pointsOfDeaths.get(i).isUsed())) {
                this.pointsOfDeaths.get(i).setUsed(true);
                returnValue = (this.pointsOfDeaths.get(i).getValue());

                break;
            }
        }

        return returnValue;
    }

    void setPointsUsed() {
        for (int i = this.kills; i < this.pointsOfDeaths.size(); i++) {
            this.pointsOfDeaths.get(i).setUsed(false);
        }
    }

    void setFrenzy() {
        this.kills = 0;
        this.pointsOfDeaths.clear();
        this.pointsOfDeaths.add(new PointsOfDeath(2));
    }
}
