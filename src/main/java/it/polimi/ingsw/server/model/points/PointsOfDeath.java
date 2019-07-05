package it.polimi.ingsw.server.model.points;

import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * class for representing the reward points for helping kill a player
 */
public class PointsOfDeath implements Serializable {

    /**
     *number of points in reward to the player for killing the dead player
     */
    private int value;
    /**
     *  checks to see if this points are already been given to another player
     */
    private boolean used = false;

    /**
     * creates the points to assign for the death of the player
     * @param value are the points that will be given to the player
     */
    public PointsOfDeath(int value) {

        this.value = value;
    }

    /**
     * checks if the points are already given to a player
     * @return if the points are already given to the player
     */
    public boolean isUsed() {

        return this.used;
    }

    /**
     * ensures that this points can now be given to another player
     */
    public void reset() {

        this.used = false;
    }

    /**
     * ensures that this points can not be given to another player
     * unless they  are reset with the reset method
     * @return the value of points that the referenced player now owns
     */
    public int getValueSetUsed() {

        this.used = true;

        return this.value;
    }

    /**
     * creates the json object  for this structure
     * @return the json object of this structure
     */
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add("used", this.used)
                .add("value", this.value)
                .build();
    }
}
