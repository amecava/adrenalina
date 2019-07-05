package it.polimi.ingsw.server.model.players.bridges;

import it.polimi.ingsw.server.model.points.PointsOfDeath;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 * this class stores all the time the linked player has died and all the points each kill
 * will give to all the players who helped kill the linked player
 */
class DeathBridge implements Serializable {

    /**
     * all  the times the linked player has died
     */
    private int kills;
    /**
     * list of points given to the players who helped kill the player
     */
    private List<PointsOfDeath> pointsOfDeaths = new ArrayList<>();

    /**
     * creating the death bridge of the linked player
     */
    DeathBridge() {

        this.kills = 0;
        this.pointsOfDeaths.add(new PointsOfDeath(8));
        this.pointsOfDeaths.add(new PointsOfDeath(6));
        this.pointsOfDeaths.add(new PointsOfDeath(4));
        this.pointsOfDeaths.add(new PointsOfDeath(2));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
    }

    /**
     * the player who owns this death bridge has died one more time
     */
    void addKill() {

        this.kills++;

        for (int i = 0; i < kills; i++) {

            this.pointsOfDeaths.get(i).getValueSetUsed();
        }
    }

    /**
     * the linked player is in frenzy mode
     */
    void setFrenzy() {

        this.kills = 0;
        this.pointsOfDeaths.clear();
        this.pointsOfDeaths.add(new PointsOfDeath(2));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
        this.pointsOfDeaths.add(new PointsOfDeath(1));
    }

    /**
     * resets all the used points from the last kill
     * sow that the points can be given to other players
     */
    void setPointsUsed() {

        this.pointsOfDeaths.stream()
                .skip(this.kills)
                .forEach(PointsOfDeath::reset);
    }

    /**
     * gives to the correct player the correct amount of points he deserves
     * for helping to kill the player
     * @return the points that  need to be assigned to the player
     */
    int assignPoints() {

        return this.pointsOfDeaths.stream()
                .skip(this.kills)
                .filter(x -> !x.isUsed())
                .findFirst()
                .map(PointsOfDeath::getValueSetUsed)
                .orElse(1);
    }

    /**
     * creates the json object of the death bridge structure for the linked player
     * @return the json object of the death bridge structure for  the linked player
     */
    JsonArray toJsonArray() {

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        this.pointsOfDeaths.stream().map(PointsOfDeath::toJsonObject).forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }
}
