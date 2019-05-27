package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.points.PointsOfDeath;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

class DeathBridge {

    private int kills;

    private List<PointsOfDeath> pointsOfDeaths = new ArrayList<>();

    DeathBridge() {

        this.kills = 0;
        this.pointsOfDeaths.add(new PointsOfDeath(8));
        this.pointsOfDeaths.add(new PointsOfDeath(6));
        this.pointsOfDeaths.add(new PointsOfDeath(4));
        this.pointsOfDeaths.add(new PointsOfDeath(2));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
    }

    void addKill() {

        this.kills++;
    }

    void setFrenzy() {

        this.kills = 0;
        this.pointsOfDeaths.clear();
        this.pointsOfDeaths.add(new PointsOfDeath(2));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
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

    JsonArray toJsonArray() {

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        this.pointsOfDeaths.stream().map(PointsOfDeath::toJsonObject).forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }
}
