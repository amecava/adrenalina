package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.points.PointsOfDeath;
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

    void setFrenzy() {

        this.kills = 0;
        this.pointsOfDeaths.clear();
        this.pointsOfDeaths.add(new PointsOfDeath(2));
    }

    void setPointsUsed() {

        this.pointsOfDeaths.stream()
                .skip(this.kills)
                .forEach(PointsOfDeath::reset);
    }

    int assignPoints() {

        return this.pointsOfDeaths.stream()
                .skip(this.kills)
                .filter(x -> !x.isUsed())
                .findFirst()
                .map(PointsOfDeath::getValueSetUsed)
                .orElse(1);
    }
}
